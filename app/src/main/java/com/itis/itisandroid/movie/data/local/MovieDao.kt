package com.itis.itisandroid.movie.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) — the class with database queries.
 *
 * Key concepts:
 * 1. **@Dao** marks the class as a data access object.
 *    Room generates the implementation of every abstract method at compile time
 *    (thanks to the KSP processor / room-compiler).
 *
 * 2. Each annotation corresponds to a CRUD operation:
 *    - **@Insert**  — Create (insert a new row)
 *    - **@Query**   — Read (any SQL SELECT)
 *    - **@Update**  — Update (update a row by primary key)
 *    - **@Delete**  — Delete (delete a row by object)
 *
 * 3. **Flow<MovieEntity>**: instead of a one-time query we return a [Flow].
 *    Room emits the current list every time the table changes,
 *    so the UI always shows fresh data — this is how reactivity works here.
 *    (The Flow comes from the room-ktx dependency and works with Coroutines.)
 *
 * 4. **suspend** functions: the DAO methods are blocking, so we run them
 *    on a background thread. A suspend function makes this explicit and
 *    lets the caller await the result inside a coroutine.
 */
@Dao
interface MovieDao {

    /**
     * READ: returns ALL movies as a reactive stream (Flow).
     *
     * Flow emits:
     *  - the current list of movies immediately,
     *  - the updated list every time a row is inserted/updated/deleted.
     */
    @Query("SELECT * FROM movies ORDER BY id DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    /**
     * READ: returns a single movie by its id as a reactive stream.
     * If the movie does not exist (or was deleted) the Flow emits null.
     */
    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: Long): Flow<MovieEntity?>

    /**
     * CREATE: inserts a new movie into the table.
     *
     * Returns the new row id (the auto-generated primary key),
     * which we can use right away if needed.
     *
     * onConflict = REPLACE: if a row with the same primary key already
     * exists it is replaced (in our case ids are auto-generated,
     * so this is just a safety measure).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity): Long

    /**
     * UPDATE: updates the row identified by the entity's primary key.
     */
    @Update
    suspend fun update(movie: MovieEntity)

    /**
     * DELETE: removes the given row from the table.
     * Room generates "DELETE FROM movies WHERE id = ?" automatically.
     */
    @Delete
    suspend fun delete(movie: MovieEntity)

    /**
     * DELETE: removes a row by its id (useful when we have only an id).
     */
    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteById(id: Long)
}
