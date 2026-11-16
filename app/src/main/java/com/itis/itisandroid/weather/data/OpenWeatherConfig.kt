package com.itis.itisandroid.weather.data

import com.itis.itisandroid.BuildConfig

/**
 * Single access point for all OpenWeatherMap API configuration.
 *
 * The secret parts (API key and base URL) are NOT stored in the source code.
 * Gradle reads them from `local.properties` — a file that is git-ignored and
 * therefore never leaves the developer's machine — and generates them into
 * the [BuildConfig] class at build time:
 *
 * ```properties
 * # local.properties
 * openweather.api.key=your_key_here
 * openweather.base.url=https://api.openweathermap.org/data/2.5/
 * ```
 *
 * HOW TO GET A FREE API KEY (required for this feature to work):
 *  1. Open https://openweathermap.org/
 *  2. Sign up for a free account.
 *  3. Go to "API Keys" and copy your key.
 *  4. Put it into `local.properties` as `openweather.api.key` and rebuild.
 *
 * Note that BuildConfig values are still compiled into the APK, so they can be
 * extracted from a release build. A real production app should keep the key on
 * a backend server instead (see README, "Limitations of the learning project").
 */
object OpenWeatherConfig {


    // "metric" => temperatures in Celsius, wind speed in m/s.
    // (Use "imperial" for Fahrenheit and miles/hour.)
    const val UNITS = "metric"

    // The /forecast endpoint gives a 5-day forecast in 3-hour steps.
    const val FORECAST_ENDPOINT = "forecast"
}
