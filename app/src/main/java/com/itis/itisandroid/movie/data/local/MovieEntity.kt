package com.itis.itisandroid.movie.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.itis.itisandroid.movie.presentation.model.Movie

/**
 * Room Entity — the "table" in our SQLite database.
 *
 * Key concepts:
 * 1. **@Entity** marks the class as a database table.
 *    Room generates a table named "movies" (lowercase class name + "s" by default).
 *    You can override the name with @Entity(tableName = "...").
 *
 * 2. **@PrimaryKey** marks the column that uniquely identifies a row.
 *    - autoGenerate = true: Room automatically assigns a unique ID
 *      (a strictly increasing integer) when a new row is inserted.
 *      We do NOT need to generate IDs ourselves anymore
 *      (previously we used System.currentTimeMillis().toString()).
 *
 * 3. Every Entity property becomes a database **column**.
 *    The column type is derived from the Kotlin type
 *    (String -> TEXT, Int -> INTEGER, etc.).
 *
 * 4. The Entity belongs to the data layer.
 *    We convert it to/from the domain model [Movie]
 *    (functions toDomain() and fromDomain() below).
 *    This keeps the data layer independent from the UI layer.
 */
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 0 means "let Room generate the id on insert"
    val name: String,
    val description: String,
) {
    /**
     * Converts the database entity to the domain model used by the UI.
     * The domain model keeps String id — it is just a technical identifier
     * and the UI does not need to know that it is a Long in the database.
     */
    fun toDomain(): Movie = Movie(
        id = id.toString(),
        name = name,
        description = description,
    )

    /**
     * Converts the domain model to a database entity.
     * For a new movie the id is left at 0 so Room auto-generates it.
     */
    companion object {
        fun fromDomain(movie: Movie): MovieEntity = MovieEntity(
            id = movie.id.toLongOrNull() ?: 0L,
            name = movie.name,
            description = movie.description,
        )
    }
}
