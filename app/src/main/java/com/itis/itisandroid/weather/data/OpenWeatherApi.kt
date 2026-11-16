package com.itis.itisandroid.weather.data

import com.itis.itisandroid.weather.data.model.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service — the "contract" with the OpenWeatherMap API.
 *
 * Key concepts:
 * 1. **It is an interface with NO implementation.** Retrofit generates the
 *    implementation at runtime: it reads the annotations, builds an HTTP
 *    request, sends it, parses the JSON response with Gson and gives you
 *    a ready Kotlin object.
 *
 * 2. **@GET("forecast")** — the path segment that will be added to the
 *    base URL (OpenWeatherConfig.BASE_URL).
 *
 * 3. **@Query** — maps a Kotlin parameter to an URL query parameter:
 *    @Query("q") city: String  ->  "?q=London"
 *
 * 4. The method returns [ForecastResponse] — the exact class that Gson
 *    will use to deserialize the JSON response (see ForecastResponse.kt).
 *
 * NOTE: `appid` and `units` are NOT declared here. They are added to every
 *    request by [ApiKeyInterceptor], a single place to change them for all
 *    current and future API methods.
 */
interface OpenWeatherApi {

    /**
     * GET https://api.openweathermap.org/data/2.5/forecast?
     *     q={city}&appid={apiKey}&units={units}
     *
     * The `appid` and `units` parts are appended by [ApiKeyInterceptor].
     */
    @GET(OpenWeatherConfig.FORECAST_ENDPOINT)
    suspend fun getForecast(
        @Query("q") city: String,
    ): ForecastResponse
}
