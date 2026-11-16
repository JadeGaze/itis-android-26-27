package com.itis.itisandroid.weather.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.itis.itisandroid.ui.components.PrimaryButton
import com.itis.itisandroid.ui.theme.Spacing
import com.itis.itisandroid.weather.presentation.model.WeatherCity
import com.itis.itisandroid.weather.presentation.model.WeatherUiState

/**
 * "Saved cities" screen.
 *
 * This is a Room showcase: deleting a city immediately removes its row
 * from the table, and the database Flow redraws both this screen and the
 * chips on the weather home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCitiesScreen(
    uiState: WeatherUiState,
    onCitySelected: (WeatherCity) -> Unit,
    onCityRemoved: (WeatherCity) -> Unit,
    onAddCity: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.weather_cities_title))
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(
                start = Spacing.md,
                end = Spacing.md,
                top = Spacing.sm,
                bottom = Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            item(key = "subtitle") {
                Text(
                    text = stringResource(R.string.weather_cities_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (uiState.savedCities.isEmpty()) {
                item(key = "empty") {
                    Box(modifier = Modifier.fillParentMaxHeight(0.5f)) {
                        EmptyView(
                            title = stringResource(R.string.weather_cities_empty_title),
                            message = stringResource(R.string.weather_cities_empty_message),
                            icon = Icons.Default.Place,
                        )
                    }
                }
            } else {
                items(items = uiState.savedCities, key = { it.name }) { city ->
                    SavedCityRow(
                        city = city,
                        selected = city.name == uiState.selectedCity?.name,
                        onSelected = { onCitySelected(city) },
                        onDelete = { onCityRemoved(city) },
                    )
                }
            }

            item(key = "add") {
                PrimaryButton(
                    text = stringResource(R.string.weather_add_city),
                    onClick = onAddCity,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Add,
                )
            }
        }
    }
}

@Composable
private fun SavedCityRow(
    city: WeatherCity,
    selected: Boolean,
    onSelected: () -> Unit,
    onDelete: () -> Unit,
) {
    AppCard(
        onClick = onSelected,
        contentPadding = PaddingValues(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (selected) {
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = stringResource(R.string.weather_cities_selected),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                if (!city.country.isNullOrBlank()) {
                    Text(
                        text = city.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.common_delete),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
