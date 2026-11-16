package com.itis.itisandroid.weather.presentation.navigation

/**
 * Weather screen routes.
 *
 * Routes are plain strings. Arguments are substituted into the template:
 * template "weather/day/{date}" + date = "2026-11-02"
 *   -> the actual route "weather/day/2026-11-02".
 *
 * dateKey is an ISO date without spaces or colons, so it can safely be
 * inserted into the path without URL encoding.
 */
object WeatherDestinations {

    /** Weather home screen (bottom-navigation tab). */
    const val HOME = "weather"

    /** Saved cities list (bottom-navigation tab). */
    const val CITIES = "weather/cities"

    /** One day's details. */
    const val DAY = "weather/day/{date}"

    /** Argument name of the DAY route. */
    const val ARG_DATE = "date"

    /** Builds the day-details route. */
    fun day(dateKey: String): String = "weather/day/$dateKey"
}
