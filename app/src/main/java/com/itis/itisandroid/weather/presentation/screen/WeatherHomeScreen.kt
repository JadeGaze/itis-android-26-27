package com.itis.itisandroid.weather.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itis.itisandroid.R
import com.itis.itisandroid.ui.components.AppSearchField
import com.itis.itisandroid.ui.components.AppTopBar
import com.itis.itisandroid.ui.components.EmptyView
import com.itis.itisandroid.ui.components.ErrorView
import com.itis.itisandroid.ui.components.LoadingView
import com.itis.itisandroid.ui.components.SectionHeader
import com.itis.itisandroid.ui.theme.Spacing
import com.itis.itisandroid.weather.presentation.components.CityChip
import com.itis.itisandroid.weather.presentation.components.CurrentWeatherCard
import com.itis.itisandroid.weather.presentation.components.ForecastDayCard
import com.itis.itisandroid.weather.presentation.components.HourlyForecastRow
import com.itis.itisandroid.weather.presentation.model.WeatherCity
import com.itis.itisandroid.weather.presentation.model.WeatherForecastDay
import com.itis.itisandroid.weather.presentation.model.WeatherUiState

/**
 * Weather home screen.
 *
 * The screen is "dumb": it does not call the network or read the database.
 * It only renders [WeatherUiState] and reports user actions upwards via
 * callbacks. All logic lives in WeatherViewModel.
 *
 * Screen states:
 *  - initial loading          -> LoadingView
 *  - error without data       -> ErrorView with a Retry button
 *  - data + network error     -> content + banner (the cache is still useful)
 *  - no city/data             -> EmptyView
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    uiState: WeatherUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onCitySelected: (WeatherCity) -> Unit,
    onDayClicked: (WeatherForecastDay) -> Unit,
    onOpenCities: () -> Unit,
    onErrorDismissed: () -> Unit,
) {
    val errorMessage = uiState.errorMessage

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.weather_title),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.common_refresh),
                        )
                    }
                    IconButton(onClick = onOpenCities) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.weather_cities_title),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading && !uiState.hasForecast -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }

            errorMessage != null && !uiState.hasForecast -> {
                ErrorView(
                    message = errorMessage,
                    modifier = Modifier.padding(paddingValues),
                    // If the user was searching for a new city, repeat the
                    // search; otherwise refresh the selected city.
                    onRetry = if (uiState.searchQuery.isNotBlank()) onSearch else onRefresh,
                    retryText = stringResource(R.string.common_retry),
                )
            }

            !uiState.hasForecast -> {
                EmptyView(
                    title = stringResource(R.string.weather_empty_title),
                    message = stringResource(R.string.weather_empty_message),
                    modifier = Modifier.padding(paddingValues),
                )
            }

            else -> {
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
                    item(key = "search") {
                        AppSearchField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            onSearch = onSearch,
                            placeholder = stringResource(R.string.weather_search_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    if (uiState.isLoading) {
                        item(key = "loading") {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    if (errorMessage != null) {
                        item(key = "error") {
                            ErrorBanner(
                                message = errorMessage,
                                onDismiss = onErrorDismissed,
                            )
                        }
                    }

                    val city = uiState.selectedCity
                    val current = uiState.current
                    if (city != null && current != null) {
                        item(key = "current") {
                            CurrentWeatherCard(city = city, current = current)
                        }
                        uiState.lastUpdatedLabel?.let { label ->
                            item(key = "updated") {
                                Text(
                                    text = stringResource(R.string.weather_updated_at, label),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    if (uiState.savedCities.isNotEmpty()) {
                        item(key = "cities_header") {
                            SectionHeader(title = stringResource(R.string.weather_saved_cities))
                        }
                        item(key = "cities") {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            ) {
                                items(
                                    items = uiState.savedCities,
                                    key = { it.name },
                                ) { savedCity ->
                                    CityChip(
                                        city = savedCity,
                                        selected = savedCity.name == uiState.selectedCity?.name,
                                        onClick = { onCitySelected(savedCity) },
                                    )
                                }
                            }
                        }
                    }

                    val todaySlots = uiState.forecast.firstOrNull()?.slots.orEmpty()
                    if (todaySlots.isNotEmpty()) {
                        item(key = "hourly_header") {
                            SectionHeader(title = stringResource(R.string.weather_hourly))
                        }
                        item(key = "hourly") {
                            HourlyForecastRow(slots = todaySlots)
                        }
                    }

                    item(key = "days_header") {
                        SectionHeader(title = stringResource(R.string.weather_5_days))
                    }

                    items(
                        items = uiState.forecast,
                        key = { it.dateKey },
                    ) { day ->
                        ForecastDayCard(
                            day = day,
                            onClick = { onDayClicked(day) },
                        )
                    }
                }
            }
        }
    }
}

/** Compact error banner shown while cached data is still visible. */
@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )

            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}
