package com.itis.itisandroid.weather.data.local

/**
 * Measurement unit used by the OpenWeatherMap forecast.
 *
 * [apiValue] is exactly what the API expects in the `units` query parameter
 * (see OpenWeatherConfig), so the enum can be used both in the network layer
 * and as a persisted Room column.
 *
 * This is the enum shown in the `Converters` example: Room stores it by name
 * in a TEXT column and restores it in [stringToTemperatureUnit].
 */
enum class TemperatureUnit(val apiValue: String) {
    /** Celsius and metres per second. */
    METRIC("metric"),

    /** Fahrenheit and miles per hour. */
    IMPERIAL("imperial"),
}
