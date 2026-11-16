package com.itis.itisandroid.movie.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itis.itisandroid.movie.data.MovieRepository
import com.itis.itisandroid.movie.presentation.model.Movie
import com.itis.itisandroid.movie.presentation.model.MovieUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MovieViewModel manages the state for the list and detail screens.
 *
 * Key ViewModel concepts:
 *
 * 1. **ViewModel is created once per Activity/NavHost** and lives as long as Activity is alive.
 *    When screen rotates, ViewModel is NOT recreated — data persists.
 *
 * 2. **StateFlow is an observable data stream** (reactive programming).
 *    UI subscribes to changes and automatically updates when data changes.
 *
 * 3. **MutableStateFlow can be modified inside ViewModel**,
 *    while StateFlow (via .asStateFlow()) is read-only for UI.
 *
 * 4. **_uiState.update { ... }** is a safe way to update state.
 *    We use copy() to update only the needed fields.
 *
 * 5. **AndroidViewModel**: extends ViewModel and receives the Application
 *    instance. We use it to create the MovieRepository (the repository
 *    needs an Android Context to open the database).
 *
 * 6. **viewModelScope**: a CoroutineScope that lives as long as the ViewModel.
 *    It is automatically cancelled when the ViewModel is cleared, so we do
 *    not leak coroutines. All database operations are launched in this scope
 *    (they run on background threads thanks to suspend functions).
 *
 * 7. **Data flow**:
 *    UI -> ViewModel (user actions) -> Repository -> Room (SQLite)
 *    Room -> Repository (Flow) -> ViewModel (StateFlow) -> UI (collectAsState)
 */
class MovieViewModel(application: Application) : AndroidViewModel(application) {

    // The repository is the single source of truth for the movies data.
    // It is created from the Application context so the database
    // outlives any Activity.
    private val repository = MovieRepository(application)

    // Private mutable state
    private val _uiState = MutableStateFlow(MovieUiState())

    // Public read-only state
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        /**
         * SUBSCRIBE to the database.
         *
         * observeAllMovies() returns a Flow that emits the current list
         * of movies and re-emits whenever the table changes.
         * We collect it inside viewModelScope — the collection runs on a
         * background thread and stops automatically when the ViewModel
         * is cleared.
         */
        viewModelScope.launch {
            repository.observeAllMovies().collect { movies ->
                _uiState.update { state -> state.copy(movies = movies) }
            }
        }
    }

    /**
     * Adds a new movie to the database.
     * Called from UI when "Add" button is pressed.
     *
     * We do NOT update the list manually: after the insert, the DAO Flow
     * re-emits the updated list and the init block above updates the state
     * and the UI by itself (reactive programming).
     */
    fun addMovie() {
        val currentState = _uiState.value

        // Validation: don't add empty movie
        if (currentState.newMovieName.isBlank()) {
            return
        }

        // The database insert is blocking, so it must run in a coroutine.
        // launch { } starts a coroutine in viewModelScope (background thread).
        viewModelScope.launch {
            repository.addMovie(
                name = currentState.newMovieName,
                description = currentState.newMovieDescription
            )

            // Clear the input fields right after saving.
            // The movie appears in the list thanks to the reactive Flow.
            _uiState.update { state ->
                state.copy(
                    newMovieName = "",
                    newMovieDescription = ""
                )
            }
        }
    }

    /**
     * Updates the new movie name (when typing in text field).
     */
    fun updateNewMovieName(name: String) {
        _uiState.update { it.copy(newMovieName = name) }
    }

    /**
     * Updates the new movie description (when typing in text field).
     */
    fun updateNewMovieDescription(description: String) {
        _uiState.update { it.copy(newMovieDescription = description) }
    }

    /**
     * Returns a reactive stream of the movie with the given id.
     * Used on the detail screen: the screen collects this Flow,
     * so the detail page always shows fresh data from the database.
     *
     * Emits null if the movie does not exist (or was deleted).
     */
    fun observeMovieById(id: String): Flow<Movie?> =
        repository.observeMovieById(id)

    /**
     * Deletes a movie from the database by its id.
     * The list screen updates automatically (reactive Flow),
     * the detail screen receives null and can navigate back.
     */
    fun deleteMovie(id: String) {
        viewModelScope.launch {
            repository.deleteMovie(id)
        }
    }
}
