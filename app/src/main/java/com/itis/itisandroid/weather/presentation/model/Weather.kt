package com.itis.itisandroid.weather.presentation.model

/**
 * Weather domain models.
 *
 * The UI layer works ONLY with these classes and knows nothing about the
 * OpenWeatherMap JSON format (DTO) or Room tables (Entity). The repository
 * converts DTO/Entity <-> domain model.
 */

/** The city whose forecast is shown. */
data class WeatherCity(
    val name: String,
    val country: String? = null,
)

/** A single 3-hour forecast slot. */
data class WeatherSlot(
    val epochSeconds: Long,
    val timeLabel: String,        // "15:00"
    val tempC: Double,
    val feelsLikeC: Double,
    val humidityPercent: Int,
    val windSpeed: Double,
    val description: String,      // "light rain"
    val iconCode: String,         // "10d" — OpenWeatherMap icon code
)

/**
 * One forecast day built from several 3-hour slots.
 *
 * @param dateKey ISO date "2026-11-02" — a stable key for navigating to
 *                the day-details screen (dateLabel is localized and varies)
 * @param slots all slots of this day (for the details screen)
 */
data class WeatherForecastDay(
    val dateKey: String,
    val dateLabel: String,        // "Mon, 2 Nov"
    val minTempC: Double,
    val maxTempC: Double,
    val humidityPercent: Int,     // average humidity
    val windSpeed: Double,        // maximum wind of the day
    val description: String,      // description from the midday slot
    val iconCode: String,
    val slots: List<WeatherSlot> = emptyList(),
)

/** Current weather — the first (nearest) forecast slot. */
data class CurrentWeather(
    val tempC: Double,
    val feelsLikeC: Double,
    val humidityPercent: Int,
    val windSpeed: Double,
    val description: String,
    val iconCode: String,
)

/**
 * State of the weather screens.
 *
 * One UiState for the whole feature — the same pattern as MovieUiState
 * for movies. Screens do not keep business state in remember, so screen
 * rotation loses nothing.
 */
data class WeatherUiState(
    val searchQuery: String = "",
    val selectedCity: WeatherCity? = null,
    val savedCities: List<WeatherCity> = emptyList(),
    val current: CurrentWeather? = null,
    val forecast: List<WeatherForecastDay> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdatedLabel: String? = null,
) {
    /** Whether a forecast (including a cached one) is already available. */
    val hasForecast: Boolean get() = current != null && forecast.isNotEmpty()
}
