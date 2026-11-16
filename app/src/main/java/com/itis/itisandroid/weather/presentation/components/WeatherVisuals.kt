package com.itis.itisandroid.weather.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Weather visual helpers.
 *
 * OpenWeatherMap returns an icon code (01d, 10n, 13d...). The learning
 * project does not ship images or an image-loading library: emoji are
 * enough to show the condition and need neither assets nor internet.
 */

/** OpenWeatherMap icon code -> emoji. */
fun weatherEmoji(iconCode: String?): String {
    val code = iconCode.orEmpty()
    return when {
        code.startsWith("01") -> "☀️" // clear sky
        code.startsWith("02") -> "🌤️" // few clouds
        code.startsWith("03") -> "⛅" // scattered clouds
        code.startsWith("04") -> "☁️" // broken clouds
        code.startsWith("09") -> "🌧️" // shower rain
        code.startsWith("10") -> "🌦️" // rain
        code.startsWith("11") -> "⛈️" // thunderstorm
        code.startsWith("13") -> "❄️" // snow
        code.startsWith("50") -> "🌫️" // mist
        else -> "🌡️"
    }
}

/** "light rain" -> "Light rain" (the API returns a lowercase description). */
fun String.capitalized(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

/** 15.6 -> "16°" — temperature without extra decimal places. */
fun Double.toTempString(): String = "${roundToInt()}°"

/** Weather emoji as regular Text (accessible to screen readers). */
@Composable
fun WeatherIcon(
    iconCode: String?,
    modifier: Modifier = Modifier,
    fontSize: Int = 32,
) {
    Text(
        text = weatherEmoji(iconCode),
        modifier = modifier,
        fontSize = fontSize.sp,
        textAlign = TextAlign.Center,
    )
}

/** Degree colors: warm temperatures use the accent, cold ones use blue. */
@Composable
fun temperatureColor(tempC: Double) = when {
    tempC >= 25 -> MaterialTheme.colorScheme.tertiary
    tempC <= 0 -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurface
}
