package com.itis.itisandroid.weather.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

/**
 * DTO (Data Transfer Object) classes for the OpenWeatherMap /forecast endpoint.
 *
 * What is a DTO?
 *  - A DTO is a plain Kotlin class that mirrors the JSON structure of an API
 *    response. It is used ONLY for network communication (data layer).
 *  - The presentation layer works with its own domain models
 *    (WeatherCity / WeatherForecastDay) that contain only the data
 *    the UI actually needs. The repository converts DTO -> domain model.
 *
 * How Gson matches JSON to these classes?
 *  - Each JSON key becomes a class property, each JSON value -> its Kotlin type.
 *  - Example: {"name": "Moscow", "country": "RU"}
 *      -> CityDto(name = "Moscow", country = "RU", ...)
 *  - @SerializedName("day") means: read the JSON key "day" into the
 *    property named [dt] (useful when the key name is not a valid
 *    Kotlin property name or you want a different name).
 *  - Properties that do not exist in the JSON simply stay null
 *    (that is why most of them are nullable).
 *
 * Full JSON example (abridged):
 * {
 *   "cod": "200",
 *   "city": { "id": 524901, "name": "Moscow", "country": "RU", "coord": {...} },
 *   "list": [
 *     {
 *       "dt": 1697000000,                 // unix time (seconds)
 *       "main": { "temp": 15.2, "temp_min": 12.1, "temp_max": 18.9, "humidity": 70 },
 *       "weather": [ { "description": "clouds", "icon": "01d" } ],
 *       "wind": { "speed": 3.4 }
 *     }
 *   ]
 * }
 */
data class ForecastResponse(
    val cod: String? = null,
    val city: CityDto? = null,
    // "list" is a list of 40 items (5 days x 3-hour steps)
    val list: List<ListItemDto>? = null,
)

/** JSON object "city" from the response. */
data class CityDto(
    val id: Long? = null,
    val name: String? = null,
    val country: String? = null,
    val population: Int? = null,
)

/** One entry of the "list" array (weather for a specific 3-hour slot). */
data class ListItemDto(
    // Unix timestamp, seconds since 1970-01-01.
    // @SerializedName because "dt" is too cryptic for a Kotlin property name.
    @SerializedName("dt")
    val dt: Long? = null,
    val main: MainDto? = null,
    val weather: List<WeatherDto>? = null,
    val wind: WindDto? = null,
)

/** JSON object "main" — the temperature information. */
data class MainDto(
    val temp: Double? = null,
    // "feels_like" — how the temperature is perceived (wind, humidity)
    @SerializedName("feels_like")
    val feelsLike: Double? = null,
    @SerialName("temp_min")
    val tempMin: Double? = null,
    val temp_max: Double? = null,
    val humidity: Int? = null,
)

/** JSON object "weather" — a list with one item that describes the condition. */
data class WeatherDto(
    val id: Int? = null,
    val main: String? = null,
    val description: String? = null,
    // Icon code, e.g. "01d" (few clouds, day), "50n" (rain, night).
    val icon: String? = null,
)

/** JSON object "wind". */
data class WindDto(
    val speed: Double? = null,
    val deg: Double? = null,
)
