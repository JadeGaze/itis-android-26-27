package com.itis.itisandroid.weather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Weather DAO: all queries against the saved_cities and
 * weather_forecast_slots tables.
 *
 * Note that read methods return Flow, so the UI updates automatically
 * after any write to the database — exactly like the movies list.
 */
@Dao
interface WeatherDao {

    /* ---------- Saved cities ---------- */

    /** All user cities; newer ones last (addedAt grows). */
    @Query("SELECT * FROM saved_cities ORDER BY addedAt ASC, name ASC")
    fun observeSavedCities(): Flow<List<SavedCityEntity>>

    /**
     * Adds a city. IGNORE: if the city is already saved, the row is not
     * overwritten — this keeps addedAt and avoids reordering the city
     * list after every forecast refresh.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: SavedCityEntity)

    @Query("DELETE FROM saved_cities WHERE name = :cityName")
    suspend fun deleteCityRow(cityName: String)

    /* ---------- Forecast cache ---------- */

    /** Forecast slots of a city ordered by time. */
    @Query(
        "SELECT * FROM weather_forecast_slots " +
            "WHERE cityName = :cityName ORDER BY epochSeconds ASC"
    )
    fun observeForecast(cityName: String): Flow<List<ForecastSlotEntity>>

    @Query("DELETE FROM weather_forecast_slots WHERE cityName = :cityName")
    suspend fun deleteForecastRows(cityName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(slots: List<ForecastSlotEntity>)

    /**
     * Atomically replaces a city's forecast cache: old slots are deleted
     * and new ones inserted in a single transaction. If the app crashes
     * midway, the database is not left half-updated.
     */
    @Transaction
    suspend fun replaceForecast(cityName: String, slots: List<ForecastSlotEntity>) {
        deleteForecastRows(cityName)
        insertForecast(slots)
    }

    /** Deletes a city together with its cache in one transaction. */
    @Transaction
    suspend fun deleteCityWithForecast(cityName: String) {
        deleteCityRow(cityName)
        deleteForecastRows(cityName)
    }
}
