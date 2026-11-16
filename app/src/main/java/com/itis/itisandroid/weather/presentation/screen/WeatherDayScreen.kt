package com.itis.itisandroid.weather.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itis.itisandroid.R
import com.itis.itisandroid.ui.components.AppCard
import com.itis.itisandroid.ui.components.AppTopBar
import com.itis.itisandroid.ui.components.EmptyView
import com.itis.itisandroid.ui.components.SectionHeader
import com.itis.itisandroid.ui.components.StatTile
import com.itis.itisandroid.ui.theme.Spacing
import com.itis.itisandroid.weather.presentation.components.ForecastSlotRow
import com.itis.itisandroid.weather.presentation.components.WeatherIcon
import com.itis.itisandroid.weather.presentation.components.capitalized
import com.itis.itisandroid.weather.presentation.components.toTempString
import com.itis.itisandroid.weather.presentation.model.WeatherUiState

/**
 * Day-details screen: a summary plus all 3-hour slots.
 *
 * Data is taken from the same UiState by dateKey — no second ViewModel
 * and no extra request are needed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDayScreen(
    uiState: WeatherUiState,
    dateKey: String,
    onBackClicked: () -> Unit,
) {
    val day = uiState.forecast.firstOrNull { it.dateKey == dateKey }

    Scaffold(
        topBar = {
            AppTopBar(
                title = day?.dateLabel ?: stringResource(R.string.weather_day_title),
                onBackClicked = onBackClicked,
            )
        },
    ) { paddingValues ->
        if (day == null) {
            EmptyView(
                title = stringResource(R.string.weather_empty_title),
                message = stringResource(R.string.weather_empty_message),
                modifier = Modifier.padding(paddingValues),
            )
        } else {
            LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(
                start = Spacing.md,
                end = Spacing.md,
                top = Spacing.sm,
                bottom = Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item(key = "summary") {
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WeatherIcon(
                            iconCode = day.iconCode,
                            modifier = Modifier.size(56.dp),
                            fontSize = 44,
                        )
                        Spacer(modifier = Modifier.width(Spacing.md))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = day.description.capitalized(),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(
                                    R.string.weather_temp_range,
                                    day.minTempC,
                                    day.maxTempC,
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(Spacing.md))

                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        StatTile(
                            label = stringResource(R.string.weather_day_min),
                            value = day.minTempC.toTempString(),
                            modifier = Modifier.weight(1f),
                        )
                        StatTile(
                            label = stringResource(R.string.weather_day_max),
                            value = day.maxTempC.toTempString(),
                            modifier = Modifier.weight(1f),
                        )
                        StatTile(
                            label = stringResource(R.string.weather_humidity),
                            value = stringResource(
                                R.string.weather_humidity_value,
                                day.humidityPercent,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            item(key = "hourly_header") {
                SectionHeader(
                    title = stringResource(R.string.weather_day_hourly),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

                items(items = day.slots, key = { it.epochSeconds }) { slot ->
                    ForecastSlotRow(slot = slot)
                }
            }
        }
    }
}
