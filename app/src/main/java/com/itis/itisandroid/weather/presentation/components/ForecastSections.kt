package com.itis.itisandroid.weather.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itis.itisandroid.R
import com.itis.itisandroid.ui.components.AppCard
import com.itis.itisandroid.ui.theme.Spacing
import com.itis.itisandroid.weather.presentation.model.WeatherForecastDay
import com.itis.itisandroid.weather.presentation.model.WeatherSlot

/**
 * Horizontal hourly forecast strip.
 * LazyRow inside LazyColumn is a normal composition: items scroll
 * horizontally independently of the vertical list.
 */
@Composable
fun HourlyForecastRow(
    slots: List<WeatherSlot>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(items = slots, key = { it.epochSeconds }) { slot ->
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(
                    modifier = Modifier
                        .width(76.dp)
                        .padding(vertical = Spacing.md, horizontal = Spacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = slot.timeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    WeatherIcon(iconCode = slot.iconCode, fontSize = 24)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = slot.tempC.toTempString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = temperatureColor(slot.tempC),
                    )
                }
            }
        }
    }
}

/** Day card in the "5-day forecast" list. Clicking it opens day details. */
@Composable
fun ForecastDayCard(
    day: WeatherForecastDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIcon(
                iconCode = day.iconCode,
                modifier = Modifier.size(40.dp),
                fontSize = 28,
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = day.dateLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = day.description.capitalized(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = stringResource(
                    R.string.weather_temp_range,
                    day.minTempC,
                    day.maxTempC,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = temperatureColor(day.maxTempC),
            )
        }
    }
}

/** A single 3-hour slot row on the day-details screen. */
@Composable
fun ForecastSlotRow(
    slot: WeatherSlot,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = slot.timeLabel,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(56.dp),
        )
        WeatherIcon(
            iconCode = slot.iconCode,
            modifier = Modifier.size(32.dp),
            fontSize = 24,
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = slot.description.capitalized(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(
                    R.string.weather_slot_details,
                    slot.humidityPercent,
                    slot.windSpeed,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = slot.tempC.toTempString(),
            style = MaterialTheme.typography.titleMedium,
            color = temperatureColor(slot.tempC),
        )
    }
}
