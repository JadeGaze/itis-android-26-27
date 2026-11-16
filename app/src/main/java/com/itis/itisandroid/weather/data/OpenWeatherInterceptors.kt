package com.itis.itisandroid.weather.data

import android.util.Log
import com.itis.itisandroid.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp interceptors — "middleware" for every HTTP call.
 *
 * An interceptor sits between the code that builds a request and the network.
 * It may inspect the request, modify it, short-circuit it (return a response
 * without going online) or decorate the response.
 *
 * Two kinds exist, and they are registered on the same OkHttpClient, so the
 * difference is in what they can see:
 *
 *  - **Application interceptor** (`addInterceptor`) — called once per logical
 *    call. It sees the request as your app built it and does not run again on
 *    redirects or retries. This is the place for authentication headers,
 *    common query parameters and logging of what the app asked for.
 *
 *  - **Network interceptor** (`addNetworkInterceptor`) — called for every
 *    actual network round-trip, including redirects and retries. This is the
 *    place to inspect the bytes that really go over the wire.
 *
 * Order matters: interceptors run in the order they are added on the way to
 * the server and in reverse order on the way back:
 *
 *     app -> [first] -> [second] -> network -> [second] -> [first] -> app
 *
 * Both examples below are application interceptors and are wired up in
 * [OpenWeatherDataSource].
 */

/**
 * Example 1: authentication + common query parameters.
 *
 * OpenWeatherMap requires `appid` and `units` in the query string of every
 * request. Instead of repeating them in each API method, the interceptor adds
 * them once for the whole client. Adding a second API endpoint later needs
 * zero changes here.
 *
 * `setQueryParameter` (not `addQueryParameter`) replaces an existing value, so
 * a repeated pass through the chain can never produce `?appid=a&appid=a`.
 */
class ApiKeyInterceptor(
    private val apiKey: String,
    private val units: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val url = original.url.newBuilder()
            .setQueryParameter("appid", apiKey)
            .setQueryParameter("units", units)
            .build()

        val request = original.newBuilder()
            .url(url)
            .header("Accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}

/**
 * Example 2: request/response logging.
 *
 * Logs the method, URL, status code and duration of every call. This is a
 * hand-written replacement for OkHttp's `HttpLoggingInterceptor` — it shows
 * exactly what an interceptor can do and adds no extra dependency.
 *
 * Security note: the `appid` query parameter is MASKED before logging. Logs
 * often end up in crash reports and bug tickets, so a secret must never be
 * written there — this is the same reason the key lives in `local.properties`
 * and not in the source code.
 *
 * The interceptor is added LAST, closest to the network, so it logs the URL
 * after [ApiKeyInterceptor] has already put `appid` into it.
 *
 * Logging is wrapped in [BuildConfig.DEBUG]: debug logs are useful while
 * developing and useless (or even harmful) in a release build, so the compiler
 * can drop those calls entirely.
 */
class WeatherLoggingInterceptor(
    private val tag: String = "WeatherApi",
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val safeUrl = maskApiKey(request.url.toString())
        val startedAt = System.nanoTime()

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            // A network failure is an expected outcome (no internet), not a
            // crash: log it and rethrow so the caller can handle it.
            if (BuildConfig.DEBUG) {
                Log.e(tag, "${request.method} $safeUrl failed: ${e.message}", e)
            }
            throw e
        }

        val elapsedMs = (System.nanoTime() - startedAt) / 1_000_000
        if (BuildConfig.DEBUG) {
            Log.d(tag, "${request.method} $safeUrl -> ${response.code} in $elapsedMs ms")
        }
        return response
    }

    /** Hides the API key value: `appid=abcd1234` -> `appid=***`. */
    private fun maskApiKey(url: String): String =
        url.replace(Regex("(appid=)[^&]+"), "\$1***")
}
