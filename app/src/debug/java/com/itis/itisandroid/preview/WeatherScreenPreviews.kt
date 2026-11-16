package com.itis.itisandroid.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.itis.itisandroid.ui.theme.ItisAndroidTheme
import com.itis.itisandroid.weather.presentation.screen.WeatherCitiesScreen
import com.itis.itisandroid.weather.presentation.screen.WeatherDayScreen
import com.itis.itisandroid.weather.presentation.screen.WeatherHomeScreen

/**
 * Previews for every weather screen and its states:
 *  - home: content, search, loading, error, cached content + error, empty, dark;
 *  - day details: found and missing day;
 *  - saved cities: list and empty.
 */

@Preview(name = "Home – content", group = "Weather", showBackground = true, heightDp = 1100)
@Composable
private fun WeatherHomeContentPreview() {
    ItisAndroidTheme {
        WeatherHomeScreen(
            uiState = WeatherPreviewData.contentState(),
            onSearchQueryChange = {},
            onSearch = {},
            onRefresh = {},
            onCitySelected = {},
            onDayClicked = {},
            onOpenCities = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(name = "Home – search query", group = "Weather", showBackground = true, heightDp = 1100)
@Composable
private fun WeatherHomeSearchPreview() {
    ItisAndroidTheme {
        WeatherHomeScreen(
            uiState = WeatherPreviewData.searchState(),
            onSearchQueryChange = {},
            onSearch = {},
            onRefresh = {},
            onCitySelected = {},
            onDayClicked = {},
            onOpenCities = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(name = "Home – loading", group = "Weather", showBackground = true, heightDp = 800)
@Composable
private fun WeatherHomeLoadingPreview() {
    ItisAndroidTheme {
        WeatherHomeScreen(
            uiState = WeatherPreviewData.loadingState(),
            onSearchQueryChange = {},
            onSearch = {},
            onRefresh = {},
            onCitySelected = {},
            onDayClicked = {},
            onOpenCities = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(name = "Home – error (no data)", group = "Weather", showBackground = true, heightDp = 800)
@Composable
private fun WeatherHomeErrorPreview() {
    ItisAndroidTheme {
        WeatherHomeScreen(
            uiState = WeatherPreviewData.errorState(),
            onSearchQueryChange = {},
            onSearch = {},
            onRefresh = {},
            onCitySelected = {},
            onDayClicked = {},
            onOpenCities = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(name = "Home – cached + error", group = "Weather", showBackground = true, heightDp = 1100)
@Composable
private fun WeatherHomeContentWithErrorPreview() {
    ItisAndroidTheme {
        WeatherHomeScreen(
            uiState = WeatherPreviewData.contentWithErrorState(),
            onSearchQueryChange = {},
            onSearch = {},
            onRefresh = {},
            onCitySelected = {},
            onDayClicked = {},
            onOpenCities = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(name = "Home – empty", group = "Weather", showBackground = true, heightDp = 800)
@Composable
private fun WeatherHomeEmptyPreview() {
    ItisAndroidTheme {
        WeatherHomeScreen(
            uiState = WeatherPreviewData.emptyState(),
            onSearchQueryChange = {},
            onSearch = {},
            onRefresh = {},
            onCitySelected = {},
            onDayClicked = {},
            onOpenCities = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(name = "Home – dark", group = "Weather", showBackground = true, heightDp = 1100)
@Composable
private fun WeatherHomeDarkPreview() {
    ItisAndroidTheme(darkTheme = true) {
        WeatherHomeScreen(
            uiState = WeatherPreviewData.contentState(),
            onSearchQueryChange = {},
            onSearch = {},
            onRefresh = {},
            onCitySelected = {},
            onDayClicked = {},
            onOpenCities = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(name = "Day details", group = "Weather", showBackground = true, heightDp = 1100)
@Composable
private fun WeatherDayPreview() {
    ItisAndroidTheme {
        WeatherDayScreen(
            uiState = WeatherPreviewData.contentState(),
            dateKey = WeatherPreviewData.forecast.first().dateKey,
            onBackClicked = {},
        )
    }
}

@Preview(name = "Day details – missing day", group = "Weather", showBackground = true, heightDp = 800)
@Composable
private fun WeatherDayMissingPreview() {
    ItisAndroidTheme {
        WeatherDayScreen(
            uiState = WeatherPreviewData.contentState(),
            dateKey = "2020-01-01",
            onBackClicked = {},
        )
    }
}

@Preview(name = "Cities – list", group = "Weather", showBackground = true, heightDp = 800)
@Composable
private fun WeatherCitiesPreview() {
    ItisAndroidTheme {
        WeatherCitiesScreen(
            uiState = WeatherPreviewData.contentState(),
            onCitySelected = {},
            onCityRemoved = {},
            onAddCity = {},
        )
    }
}

@Preview(name = "Cities – empty", group = "Weather", showBackground = true, heightDp = 800)
@Composable
private fun WeatherCitiesEmptyPreview() {
    ItisAndroidTheme {
        WeatherCitiesScreen(
            uiState = WeatherPreviewData.emptyState(),
            onCitySelected = {},
            onCityRemoved = {},
            onAddCity = {},
        )
    }
}
