package com.itis.itisandroid.movie.presentation.model

/**
 * Movie — the DOMAIN model of a movie.
 *
 * Note the difference between this class and [com.itis.itisandroid.movie.data.local.MovieEntity]:
 * - Movie is what the UI and the ViewModel work with (domain layer).
 * - MovieEntity is what the database stores (data layer).
 * The repository converts between them, so the layers stay independent.
 *
 * The id is a String here: it is only a technical identifier for
 * navigation (e.g. the route "list/{id}"), the UI never manipulates it.
 * In the database the id is a Long (auto-generated), see MovieEntity.
 */
data class Movie(
    val id: String,
    val name: String,
    val description: String,
)

/**
 * UiState represents the state of the UI screen.
 * Data class contains all data needed by the screen to render.
 *
 * The list of movies is empty at start: it is filled by the reactive
 * stream from the database (see MovieViewModel.init).
 * Previously the list was filled with hardcoded sample movies —
 * now the database itself is seeded with sample data on first launch
 * (see AppDatabase callback).
 */
data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val newMovieName: String = "",
    val newMovieDescription: String = "",
)
