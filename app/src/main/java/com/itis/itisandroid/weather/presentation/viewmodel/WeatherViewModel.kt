package com.itis.itisandroid.weather.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itis.itisandroid.R
import com.itis.itisandroid.weather.data.WeatherRepository
import com.itis.itisandroid.weather.presentation.model.CurrentWeather
import com.itis.itisandroid.weather.presentation.model.WeatherCity
import com.itis.itisandroid.weather.presentation.model.WeatherForecastDay
import com.itis.itisandroid.weather.presentation.model.WeatherSlot
import com.itis.itisandroid.weather.presentation.model.WeatherUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.net.ssl.SSLException
import kotlin.math.abs

/**
 * Weather ViewModel.
 *
 * Design (same as MovieViewModel, but with a network):
 *
 *  1. [selectedCityName] — "which city is selected right now". It is a
 *     separate MutableStateFlow because switching the city means
 *     RESUBSCRIBING to another database stream: flatMapLatest does that
 *     automatically.
 *
 *  2. Saved cities and the forecast come from Room as Flows. The ViewModel
 *     combines them into a single [WeatherUiState], and Compose reads the
 *     state through collectAsState.
 *
 *  3. The network is called only on a user action (search, refresh, city
 *     selection). The result is saved to the database and the UI updates
 *     by itself.
 *
 * No DI: the repository is constructed manually from the Application
 * context. That is enough for a learning project; a large app would use
 * Hilt/Koin here.
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository(application)

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    /** Name of the selected city; null means nothing is selected yet. */
    private val selectedCityName = MutableStateFlow<String?>(null)

    /** The initial auto-refresh runs only once. */
    private var initialRefreshDone = false

    init {
        observeSavedCities()
        observeForecastOfSelectedCity()
    }

    /* ---------- Database streams ---------- */

    private fun observeSavedCities() {
        viewModelScope.launch {
            repository.observeSavedCities().collect { cities ->
                _uiState.update { it.copy(savedCities = cities) }

                val currentName = selectedCityName.value
                val selectedFromDb = cities.firstOrNull { it.name == currentName }

                when {
                    // The selected city was deleted (or nothing is selected) —
                    // switch to the first available city.
                    selectedFromDb == null -> {
                        val fallback = cities.firstOrNull()
                        selectedCityName.value = fallback?.name
                        _uiState.update {
                            it.copy(
                                selectedCity = fallback,
                                // The city changed — do not show the old forecast;
                                // the new one will arrive from the database.
                                forecast = emptyList(),
                                current = null,
                            )
                        }
                    }

                    // The city still exists — sync its data (for example,
                    // the country returned by the API after the first search).
                    else -> {
                        _uiState.update { it.copy(selectedCity = selectedFromDb) }
                    }
                }

                // First launch: refresh the selected city's forecast at once.
                val cityToRefresh = selectedCityName.value
                if (!initialRefreshDone && cityToRefresh != null) {
                    initialRefreshDone = true
                    refreshCity(cityToRefresh)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeForecastOfSelectedCity() {
        viewModelScope.launch {
            selectedCityName
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { cityName -> repository.observeForecast(cityName) }
                .collect { days ->
                    _uiState.update { state ->
                        state.copy(
                            forecast = days,
                            current = days.toCurrentWeather(),
                        )
                    }
                }
        }
    }

    /* ---------- User actions ---------- */

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * City search: network -> Room cache -> the city is saved to the list.
     * On failure the cache is untouched and a readable message is shown.
     */
    fun search() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return
        // Search loads the forecast itself, so the initial auto-refresh
        // is no longer needed (otherwise there would be an extra request).
        initialRefreshDone = true
        refreshCity(query, addToSavedCities = true)
    }

    /** Re-refreshes the selected city's forecast. */
    fun refresh() {
        val city = _uiState.value.selectedCity ?: return
        refreshCity(city.name)
    }

    /** Selects a city from the saved list. */
    fun selectCity(city: WeatherCity) {
        selectedCityName.value = city.name
        _uiState.update {
            it.copy(
                selectedCity = city,
                errorMessage = null,
                lastUpdatedLabel = null,
                // While the new city is loading, do not show the old weather.
                forecast = emptyList(),
                current = null,
            )
        }
        refreshCity(city.name)
    }

    /** Removes a city together with its cache. */
    fun removeCity(city: WeatherCity) {
        viewModelScope.launch {
            repository.deleteCity(city.name)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /* ---------- Internal logic ---------- */

    private fun refreshCity(cityName: String, addToSavedCities: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.refreshForecast(cityName)
                .onSuccess { city ->
                    // Switch the selection first, then save the city:
                    // otherwise the saved_cities subscription may react to
                    // the new list before the selected city changes.
                    selectedCityName.value = city.name
                    _uiState.update {
                        it.copy(
                            selectedCity = city,
                            isLoading = false,
                            errorMessage = null,
                            searchQuery = "",
                            lastUpdatedLabel = currentTimeLabel(),
                        )
                    }
                    if (addToSavedCities) {
                        repository.saveCity(city)
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "Could not refresh forecast for \"$cityName\"", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage(),
                        )
                    }
                }
        }
    }

    private fun currentTimeLabel(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    /** Turns a network error into a readable, resource-backed message. */
    private fun Throwable.toUserMessage(): String {
        val context = getApplication<Application>()
        return when (this) {
            // SSLException is an IOException, so it must be checked first:
            // this is not "no internet", it is an untrusted certificate.
            is SSLException ->
                context.getString(R.string.weather_error_tls)

            is UnknownHostException, is IOException ->
                context.getString(R.string.weather_error_no_internet)

            is HttpException -> when (code()) {
                401 -> context.getString(R.string.weather_error_invalid_key)
                404 -> context.getString(R.string.weather_error_city_not_found)
                else -> context.getString(R.string.weather_error_unknown)
            }

            else -> context.getString(R.string.weather_error_unknown)
        }
    }

    private companion object {
        const val TAG = "WeatherViewModel"
    }
}

/**
 * Current weather — the forecast slot closest to "now".
 * If the cache is stale, the closest slot is the latest available one.
 */
private fun List<WeatherForecastDay>.toCurrentWeather(): CurrentWeather? {
    val nowSeconds = System.currentTimeMillis() / 1_000
    val slot: WeatherSlot = asSequence()
        .flatMap { it.slots.asSequence() }
        .minByOrNull { abs(it.epochSeconds - nowSeconds) }
        ?: return null

    return CurrentWeather(
        tempC = slot.tempC,
        feelsLikeC = slot.feelsLikeC,
        humidityPercent = slot.humidityPercent,
        windSpeed = slot.windSpeed,
        description = slot.description,
        iconCode = slot.iconCode,
    )
}
