package com.itis.itisandroid.preview

import com.itis.itisandroid.movie.presentation.model.Movie
import com.itis.itisandroid.movie.presentation.model.MovieUiState

/**
 * Mock data for the movie screen previews.
 *
 * Kept in the debug source set, so previews and their fixtures never
 * reach a release build.
 */
object MoviePreviewData {

    val movies: List<Movie> = listOf(
        Movie(
            id = "1",
            name = "Interstellar",
            description = "A team of explorers travel through a wormhole in space " +
                "in an attempt to ensure humanity's survival.",
        ),
        Movie(
            id = "2",
            name = "The Grand Budapest Hotel",
            description = "A writer recounts the adventures of a legendary concierge " +
                "and his most trusted lobby boy.",
        ),
        Movie(
            id = "3",
            name = "Spirited Away",
            description = "A young girl wanders into a world ruled by gods, witches " +
                "and spirits.",
        ),
    )

    val listState: MovieUiState = MovieUiState(movies = movies)

    val emptyListState: MovieUiState = MovieUiState()

    val addMovieState: MovieUiState = MovieUiState(
        newMovieName = "Dune",
        newMovieDescription = "A noble family becomes embroiled in a war for control " +
            "over the galaxy's most valuable asset.",
    )

    val addMovieEmptyState: MovieUiState = MovieUiState()
}
