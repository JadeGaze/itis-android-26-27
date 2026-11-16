package com.itis.itisandroid.weather.data

import android.content.Context
import com.itis.itisandroid.data.local.AppDatabase
import com.itis.itisandroid.weather.data.local.ForecastSlotEntity
import com.itis.itisandroid.weather.data.local.SavedCityEntity
import com.itis.itisandroid.weather.data.model.ListItemDto
import com.itis.itisandroid.weather.presentation.model.WeatherCity
import com.itis.itisandroid.weather.presentation.model.WeatherForecastDay
import com.itis.itisandroid.weather.presentation.model.WeatherSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Weather repository — the only place where NETWORK and DATABASE meet.
 *
 * Refresh flow (offline-first):
 *  1. the UI asks for a forecast -> [refreshForecast] calls Retrofit;
 *  2. a successful response is saved to Room ([ForecastSlotEntity]);
 *  3. the screen is subscribed to [observeForecast] and receives data
 *     from the DATABASE.
 *
 * Thanks to step 3 the UI does not care where the data came from.
 * Without a network the database keeps serving the last successful
 * forecast, and the ViewModel only shows an error message.
 *
 * Mapping also happens here:
 *  DTO (JSON) -> Entity (table) -> Domain model (UI).
 */
class WeatherRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).weatherDao()

    /** Saved cities as a reactive stream of domain models. */
    fun observeSavedCities(): Flow<List<WeatherCity>> =
        dao.observeSavedCities().map { entities ->
            entities.map { WeatherCity(name = it.name, country = it.country) }
        }

    /**
     * Cached forecast of a city grouped by days.
     * An empty list means no data has been saved for the city yet.
     */
    fun observeForecast(cityName: String): Flow<List<WeatherForecastDay>> =
        dao.observeForecast(cityName).map { slots -> slots.toForecastDays() }

    /**
     * Loads a fresh forecast from the network and stores it in the database.
     *
     * @return Result.success(city) — the canonical city name from the API
     *         response (the user may type "moscow", the API returns "Moscow");
     *         Result.failure(exception) — network/key/city error, the cache
     *         is left untouched.
     */
    suspend fun refreshForecast(query: String): Result<WeatherCity> = runCatching {
        val response = OpenWeatherDataSource.getForecast(query)

        val city = WeatherCity(
            name = response.city?.name ?: query.trim().replaceFirstChar { it.uppercase() },
            country = response.city?.country,
        )

        val slots = response.list.orEmpty()
            .mapNotNull { it.toEntity(cityName = city.name) }
            .sortedBy { it.epochSeconds }

        // Nothing to save means an error, not an empty forecast.
        check(slots.isNotEmpty()) { "The API returned an empty forecast" }

        // The fresh cache is written to the database; Room notifies
        // subscribers and observeForecast immediately emits new data.
        // The city itself is added to the saved list by the ViewModel through
        // saveCity() — only after it switches the current selection.
        dao.replaceForecast(cityName = city.name, slots = slots)
        city
    }

    /** Adds a city to the saved list (repeated calls have no effect). */
    suspend fun saveCity(city: WeatherCity) {
        dao.insertCity(
            SavedCityEntity(
                name = city.name,
                country = city.country,
                addedAt = Date(),
            )
        )
    }

    /** Removes a city from the list together with its cached forecast. */
    suspend fun deleteCity(cityName: String) {
        dao.deleteCityWithForecast(cityName)
    }

    /* ---------- Mapping DTO -> Entity ---------- */

    private fun ListItemDto.toEntity(cityName: String): ForecastSlotEntity? {
        val timestamp = dt ?: return null
        val temperature = main ?: return null
        val condition = weather?.firstOrNull()

        return ForecastSlotEntity(
            cityName = cityName,
            epochSeconds = timestamp,
            tempC = temperature.temp ?: 0.0,
            feelsLikeC = temperature.feelsLike ?: temperature.temp ?: 0.0,
            humidityPercent = temperature.humidity ?: 0,
            windSpeed = wind?.speed ?: 0.0,
            description = condition?.description ?: "",
            iconCode = condition?.icon ?: "",
        )
    }

    /* ---------- Mapping Entity -> Domain ---------- */

    /**
     * Groups 3-hour slots by calendar day and computes aggregates:
     * min/max temperature, average humidity, maximum wind.
     */
    private fun List<ForecastSlotEntity>.toForecastDays(): List<WeatherForecastDay> {
        if (isEmpty()) return emptyList()

        val dateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateLabelFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return groupBy { slot ->
            dateKeyFormat.format(Date(slot.epochSeconds * 1_000))
        }
            .toSortedMap()
            .map { (dateKey, daySlots) ->
                val sorted = daySlots.sortedBy { it.epochSeconds }
                val slots = sorted.map { it.toDomain(timeFormat) }
                // The slot near the middle of the day describes it best.
                val representative = sorted[sorted.size / 2]

                WeatherForecastDay(
                    dateKey = dateKey,
                    dateLabel = dateLabelFormat.format(Date(sorted.first().epochSeconds * 1_000)),
                    minTempC = sorted.minOf { it.tempC },
                    maxTempC = sorted.maxOf { it.tempC },
                    humidityPercent = sorted.map { it.humidityPercent }.average().toInt(),
                    windSpeed = sorted.maxOf { it.windSpeed },
                    description = representative.description,
                    iconCode = representative.iconCode,
                    slots = slots,
                )
            }
    }

    private fun ForecastSlotEntity.toDomain(timeFormat: SimpleDateFormat) = WeatherSlot(
        epochSeconds = epochSeconds,
        timeLabel = timeFormat.format(Date(epochSeconds * 1_000)),
        tempC = tempC,
        feelsLikeC = feelsLikeC,
        humidityPercent = humidityPercent,
        windSpeed = windSpeed,
        description = description,
        iconCode = iconCode,
    )
}
