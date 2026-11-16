package com.itis.itisandroid.weather.presentation.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.itis.itisandroid.weather.presentation.model.WeatherCity

/**
 * Saved-city chip. The selected city is highlighted.
 * The chip row is a quick way to switch cities without opening the cities screen.
 */
@Composable
fun CityChip(
    city: WeatherCity,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = city.name,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
}
