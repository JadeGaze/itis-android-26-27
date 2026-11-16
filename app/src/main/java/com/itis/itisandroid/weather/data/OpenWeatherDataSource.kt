package com.itis.itisandroid.weather.data

import com.itis.itisandroid.BuildConfig
import com.itis.itisandroid.weather.data.model.ForecastResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Remote data source — the class that actually TALKS to the API.
 *
 * It is a singleton (object) that creates the Retrofit instance once and
 * exposes the API calls. The repository uses it, the ViewModel never
 * touches Retrofit directly.
 *
 * How Retrofit is built:
 *  OkHttpClient.Builder()
 *      .addInterceptor(...)    — middleware for every HTTP call (see below)
 *      .build()
 *
 *  Retrofit.Builder()
 *      .baseUrl(...)           — the base URL for all requests
 *      .client(okHttpClient)   — the HTTP client that actually sends requests
 *      .addConverterFactory(
 *          GsonConverterFactory.create()
 *      )                       — "how to convert JSON <-> Kotlin objects"
 *      .build()
 *
 *  create(OpenWeatherApi::class.java)
 *      — asks Retrofit to generate the implementation of our interface
 *      (this is where the "magic" happens: Retrofit inspects the
 *      @GET / @Query annotations at runtime).
 *
 * Why the data source is a separate class from the repository?
 *  - The data source knows HOW to talk to OpenWeatherMap (URL, HTTP, JSON).
 *  - The repository knows WHAT data the app needs and converts the DTOs
 *    into domain models. If tomorrow we add a second API (e.g. geocoding),
 *    we add another data source and combine both in the same repository.
 */
object OpenWeatherDataSource {

    /**
     * Shared OkHttp client — the transport under Retrofit.
     *
     * Interceptors are added in this order:
     *  1. [ApiKeyInterceptor] — appends `appid` and `units` to every request;
     *  2. [WeatherLoggingInterceptor] — logs the already-built URL and the
     *     response, so the log shows exactly what goes to the server.
     *
     * On the way back the order is reversed: the logging interceptor sees the
     * response first, then the API-key interceptor passes it to Retrofit.
     */
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                ApiKeyInterceptor(
                    apiKey = BuildConfig.OPENWEATHER_API_KEY,
                    units = OpenWeatherConfig.UNITS,
                )
            )
            .addInterceptor(WeatherLoggingInterceptor())
            .build()
    }

    // Retrofit instance. Built once, reused for all requests.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.OPENWEATHER_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // The generated implementation of our API interface
    private val api: OpenWeatherApi by lazy {
        retrofit.create(OpenWeatherApi::class.java)
    }

    /**
     * Requests the 5-day forecast for the given city.
     *
     * The method is suspend: Retrofit does the network call on a
     * background thread and suspends the coroutine until the answer
     * arrives. On failure (no internet, wrong city, bad API key) it
     * throws an exception, which the repository catches.
     *
     * @param city the city name, e.g. "London" or "Moscow"
     */
    suspend fun getForecast(city: String): ForecastResponse {
        // appid and units are added by ApiKeyInterceptor, not here.
        return api.getForecast(city = city)
    }
}
