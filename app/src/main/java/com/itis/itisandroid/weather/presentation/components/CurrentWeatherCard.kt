package com.itis.itisandroid.weather.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itis.itisandroid.R
import com.itis.itisandroid.ui.components.StatTile
import com.itis.itisandroid.ui.theme.Spacing
import com.itis.itisandroid.weather.presentation.model.CurrentWeather
import com.itis.itisandroid.weather.presentation.model.WeatherCity

/**
 * Large "weather now" card: a gradient of theme colors, a huge temperature
 * (displayLarge), the description and two metrics.
 *
 * Every color comes from MaterialTheme, so the card automatically looks
 * right in both light and dark themes.
 */
@Composable
fun CurrentWeatherCard(
    city: WeatherCity,
    current: CurrentWeather,
    modifier: Modifier = Modifier,
) {
    val background = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
        ),
    )
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(background)
            .padding(Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor,
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = current.description.capitalized(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor.copy(alpha = 0.75f),
                )
            }
            WeatherIcon(
                iconCode = current.iconCode,
                modifier = Modifier.size(64.dp),
                fontSize = 48,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = current.tempC.toTempString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light,
            color = contentColor,
        )
        Text(
            text = stringResource(
                R.string.weather_feels_like_value,
                current.feelsLikeC.toTempString(),
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor.copy(alpha = 0.75f),
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = stringResource(R.string.weather_humidity),
                value = stringResource(
                    R.string.weather_humidity_value,
                    current.humidityPercent,
                ),
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
            StatTile(
                label = stringResource(R.string.weather_wind),
                value = stringResource(
                    R.string.weather_wind_value,
                    current.windSpeed,
                ),
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
