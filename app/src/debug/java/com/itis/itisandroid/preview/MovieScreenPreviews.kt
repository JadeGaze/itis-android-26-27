package com.itis.itisandroid.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.itis.itisandroid.movie.presentation.screen.AddMovieScreen
import com.itis.itisandroid.movie.presentation.screen.BadAddMovieScreen
import com.itis.itisandroid.movie.presentation.screen.DetailsScreen
import com.itis.itisandroid.movie.presentation.screen.ListScreen
import com.itis.itisandroid.ui.theme.ItisAndroidTheme

/**
 * Previews for every movie screen and its states.
 *
 * Open the "Preview" tool window in Android Studio to see them.
 */

@Preview(name = "List – content", group = "Movies", showBackground = true, heightDp = 800)
@Composable
private fun ListScreenContentPreview() {
    ItisAndroidTheme {
        ListScreen(
            items = MoviePreviewData.movies,
            onItemClicked = {},
            onAddMovieClicked = {},
        )
    }
}

@Preview(name = "List – empty", group = "Movies", showBackground = true, heightDp = 800)
@Composable
private fun ListScreenEmptyPreview() {
    ItisAndroidTheme {
        ListScreen(
            items = MoviePreviewData.emptyListState.movies,
            onItemClicked = {},
            onAddMovieClicked = {},
        )
    }
}

@Preview(name = "Details", group = "Movies", showBackground = true, heightDp = 900)
@Composable
private fun DetailsScreenPreview() {
    ItisAndroidTheme {
        DetailsScreen(
            movie = MoviePreviewData.movies.first(),
            onBackClicked = {},
            onDeleteClicked = {},
        )
    }
}

@Preview(name = "Add – filled", group = "Movies", showBackground = true, heightDp = 900)
@Composable
private fun AddMovieFilledPreview() {
    ItisAndroidTheme {
        AddMovieScreen(
            uiState = MoviePreviewData.addMovieState,
            onNameChange = {},
            onDescriptionChange = {},
            onAddMovie = {},
            onBackClicked = {},
        )
    }
}

@Preview(name = "Add – empty", group = "Movies", showBackground = true, heightDp = 900)
@Composable
private fun AddMovieScreenEmptyPreview() {
    ItisAndroidTheme {
        AddMovieScreen(
            uiState = MoviePreviewData.addMovieEmptyState,
            onNameChange = {},
            onDescriptionChange = {},
            onAddMovie = {},
            onBackClicked = {},
        )
    }
}

@Preview(name = "Add – bad example", group = "Movies", showBackground = true, heightDp = 900)
@Composable
private fun BadAddMovieScreenPreview() {
    ItisAndroidTheme {
        BadAddMovieScreen(
            onAddMovie = {},
            onBackClicked = {},
        )
    }
}

@Preview(name = "List – dark", group = "Movies", showBackground = true, heightDp = 800)
@Composable
private fun ListScreenDarkPreview() {
    ItisAndroidTheme(darkTheme = true) {
        ListScreen(
            items = MoviePreviewData.movies,
            onItemClicked = {},
            onAddMovieClicked = {},
        )
    }
}
