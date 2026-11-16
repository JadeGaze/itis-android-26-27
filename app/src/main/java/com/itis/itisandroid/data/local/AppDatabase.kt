package com.itis.itisandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.itis.itisandroid.movie.data.local.MovieDao
import com.itis.itisandroid.movie.data.local.MovieEntity
import com.itis.itisandroid.weather.data.local.ForecastSlotEntity
import com.itis.itisandroid.weather.data.local.SavedCityEntity
import com.itis.itisandroid.weather.data.local.WeatherDao

/**
 * THE SINGLE APP DATABASE.
 *
 * Previously it contained only the movies table; now one database holds
 * every feature:
 *  - movies                 — movies;
 *  - saved_cities           — saved weather cities;
 *  - weather_forecast_slots — cached forecast.
 *
 * One database per app is the standard practice: fewer files, one
 * transaction can touch data from several features, simpler migrations.
 * The feature code stays separated: movies and weather each work with
 * their own DAO.
 *
 * version = 1 because the database is created anew under a new name
 * "itis_db" (the old movies_db is no longer used).
 *
 * fallbackToDestructiveMigration() is a simplification for the learning
 * project: when the schema changes the old database is dropped.
 * Production code needs real Migration(1, 2) { ... } implementations.
 *
 * @TypeConverters registers [Converters] for the whole database, so every
 * entity may declare Date, List<String> or TemperatureUnit properties: Room
 * uses the converter to store them in a supported column type.
 */
@TypeConverters(Converters::class)
@Database(
    entities = [
        MovieEntity::class,
        SavedCityEntity::class,
        ForecastSlotEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun weatherDao(): WeatherDao

    companion object {
        const val DB_NAME = "itis_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Thread-safe singleton: one database per app process. */
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): AppDatabase {
            // Seed the database on first creation so screens are not empty
            // on the very first launch.
            val callback = object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(
                        "INSERT INTO movies (name, description) VALUES ('Movie 1', 'description')"
                    )
                    db.execSQL(
                        "INSERT INTO movies (name, description) VALUES ('Movie 2', 'description')"
                    )
                    // Starting city for the weather screen.
                    db.execSQL(
                        "INSERT INTO saved_cities (name, country, addedAt) " +
                            "VALUES ('Moscow', 'RU', 0)"
                    )
                }
            }

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME,
            )
                .addCallback(callback)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
