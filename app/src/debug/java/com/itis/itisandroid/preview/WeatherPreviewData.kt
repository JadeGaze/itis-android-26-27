package com.itis.itisandroid.preview

import com.itis.itisandroid.weather.presentation.model.CurrentWeather
import com.itis.itisandroid.weather.presentation.model.WeatherCity
import com.itis.itisandroid.weather.presentation.model.WeatherForecastDay
import com.itis.itisandroid.weather.presentation.model.WeatherSlot
import com.itis.itisandroid.weather.presentation.model.WeatherUiState

/**
 * Mock data for the weather screen previews.
 *
 * Covers every UI state: content, loading, error without data, cached
 * content with an error banner, and the empty state. Kept in the debug
 * source set so it never ships in a release build.
 */
object WeatherPreviewData {

    val moscow = WeatherCity(name = "Moscow", country = "RU")
    val london = WeatherCity(name = "London", country = "GB")
    val tokyo = WeatherCity(name = "Tokyo", country = "JP")
    val savedCities = listOf(moscow, london, tokyo)

    private const val BASE_EPOCH = 1_793_000_000L
    private const val THREE_HOURS = 10_800L

    private fun slot(
        index: Int,
        time: String,
        temp: Double,
        feelsLike: Double,
        humidity: Int,
        wind: Double,
        description: String,
        icon: String,
    ) = WeatherSlot(
        epochSeconds = BASE_EPOCH + index * THREE_HOURS,
        timeLabel = time,
        tempC = temp,
        feelsLikeC = feelsLike,
        humidityPercent = humidity,
        windSpeed = wind,
        description = description,
        iconCode = icon,
    )

    val todaySlots: List<WeatherSlot> = listOf(
        slot(0, "00:00", -5.0, -9.0, 84, 2.8, "light snow", "13n"),
        slot(1, "03:00", -6.0, -10.0, 86, 2.4, "light snow", "13n"),
        slot(2, "06:00", -4.0, -8.0, 83, 3.0, "light snow", "13d"),
        slot(3, "09:00", -2.0, -6.0, 78, 3.4, "overcast clouds", "04d"),
        slot(4, "12:00", 0.0, -4.0, 72, 3.9, "overcast clouds", "04d"),
        slot(5, "15:00", 1.0, -3.0, 70, 4.2, "broken clouds", "03d"),
        slot(6, "18:00", -2.0, -6.0, 76, 3.6, "broken clouds", "03n"),
        slot(7, "21:00", -4.0, -8.0, 81, 3.1, "light snow", "13n"),
    )

    val tomorrowSlots: List<WeatherSlot> = listOf(
        slot(8, "00:00", -6.0, -10.0, 85, 2.9, "light snow", "13n"),
        slot(9, "03:00", -7.0, -11.0, 87, 2.5, "light snow", "13n"),
        slot(10, "06:00", -5.0, -9.0, 84, 3.0, "light snow", "13d"),
        slot(11, "09:00", -3.0, -7.0, 79, 3.3, "snow", "13d"),
        slot(12, "12:00", -1.0, -5.0, 74, 3.7, "snow", "13d"),
        slot(13, "15:00", 0.0, -4.0, 72, 4.0, "overcast clouds", "04d"),
        slot(14, "18:00", -3.0, -7.0, 78, 3.5, "overcast clouds", "04n"),
        slot(15, "21:00", -5.0, -9.0, 82, 3.1, "light snow", "13n"),
    )

    val dayAfterSlots: List<WeatherSlot> = listOf(
        slot(16, "00:00", -3.0, -7.0, 80, 2.7, "light snow", "13n"),
        slot(17, "03:00", -4.0, -8.0, 83, 2.3, "light snow", "13n"),
        slot(18, "06:00", -2.0, -6.0, 79, 3.0, "overcast clouds", "04d"),
        slot(19, "09:00", 1.0, -3.0, 73, 3.8, "overcast clouds", "04d"),
        slot(20, "12:00", 3.0, -1.0, 66, 4.4, "few clouds", "02d"),
        slot(21, "15:00", 4.0, 0.0, 63, 4.6, "few clouds", "02d"),
        slot(22, "18:00", 1.0, -3.0, 71, 3.6, "scattered clouds", "03n"),
        slot(23, "21:00", -1.0, -5.0, 77, 3.2, "scattered clouds", "03n"),
    )

    val forecast: List<WeatherForecastDay> = listOf(
        WeatherForecastDay(
            dateKey = "2026-11-02",
            dateLabel = "Mon, 2 Nov",
            minTempC = -6.0,
            maxTempC = 1.0,
            humidityPercent = 78,
            windSpeed = 4.2,
            description = "overcast clouds",
            iconCode = "04d",
            slots = todaySlots,
        ),
        WeatherForecastDay(
            dateKey = "2026-11-03",
            dateLabel = "Tue, 3 Nov",
            minTempC = -7.0,
            maxTempC = 0.0,
            humidityPercent = 80,
            windSpeed = 4.0,
            description = "snow",
            iconCode = "13d",
            slots = tomorrowSlots,
        ),
        WeatherForecastDay(
            dateKey = "2026-11-04",
            dateLabel = "Wed, 4 Nov",
            minTempC = -4.0,
            maxTempC = 4.0,
            humidityPercent = 74,
            windSpeed = 4.6,
            description = "few clouds",
            iconCode = "02d",
            slots = dayAfterSlots,
        ),
    )

    val current = CurrentWeather(
        tempC = -3.0,
        feelsLikeC = -7.0,
        humidityPercent = 82,
        windSpeed = 3.4,
        description = "light snow",
        iconCode = "13d",
    )

    /** Happy path: current weather, hourly strip, saved cities and 5 days. */
    fun contentState(): WeatherUiState = WeatherUiState(
        selectedCity = moscow,
        savedCities = savedCities,
        current = current,
        forecast = forecast,
        lastUpdatedLabel = "14:32",
    )

    /** First load: nothing in the cache yet. */
    fun loadingState(): WeatherUiState = WeatherUiState(
        selectedCity = moscow,
        savedCities = savedCities,
        isLoading = true,
    )

    /** Network failed and there is no cached forecast. */
    fun errorState(): WeatherUiState = WeatherUiState(
        selectedCity = moscow,
        savedCities = savedCities,
        errorMessage = "No internet connection. Showing the last saved forecast, if any.",
    )

    /** Cached data is visible, but the last refresh failed. */
    fun contentWithErrorState(): WeatherUiState = contentState().copy(
        errorMessage = "No internet connection. Showing the last saved forecast, if any.",
    )

    /** Nothing selected, no cities, no data. */
    fun emptyState(): WeatherUiState = WeatherUiState()

    /** Ready-to-edit search query. */
    fun searchState(): WeatherUiState = contentState().copy(
        searchQuery = "Amsterdam",
    )
}
