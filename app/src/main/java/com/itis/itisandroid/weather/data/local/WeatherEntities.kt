package com.itis.itisandroid.weather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Weather Room entities — these are DATABASE TABLES.
 *
 * Table 1: saved_cities — cities added by the user.
 * Table 2: weather_forecast_slots — forecast cache (3-hour slots).
 *
 * Why cache the forecast in the database when there is a network?
 *  - without the internet the screen keeps showing the latest data;
 *  - on the next launch the app shows the forecast immediately,
 *    without waiting for the network;
 *  - this is the classic offline-first approach: the network is the
 *    source of updates, the database is the source of truth for the UI.
 */

/**
 * A city in the "saved" list (primary key is the city name).
 *
 * [addedAt] is a [Date], which SQLite cannot store directly: the registered
 * `Converters` class turns it into epoch milliseconds in an INTEGER column.
 * That is why the DAO can still ORDER BY addedAt — the comparison happens on
 * plain numbers.
 */
@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey val name: String,
    val country: String? = null,
    // Time the city was added: used for stable list ordering.
    val addedAt: Date = Date(0L),
)

/**
 * One forecast slot.
 *
 * The composite primary key (cityName + epochSeconds) prevents saving
 * the same slot twice on a refresh. onConflict = REPLACE in the DAO
 * turns this into an upsert.
 */
@Entity(
    tableName = "weather_forecast_slots",
    primaryKeys = ["cityName", "epochSeconds"],
)
data class ForecastSlotEntity(
    val cityName: String,
    // Unix time of the slot in seconds (as returned by the API).
    val epochSeconds: Long,
    val tempC: Double,
    val feelsLikeC: Double,
    val humidityPercent: Int,
    val windSpeed: Double,
    val description: String,
    val iconCode: String,
)
