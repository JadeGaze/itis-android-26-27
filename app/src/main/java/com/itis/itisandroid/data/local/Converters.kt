package com.itis.itisandroid.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itis.itisandroid.weather.data.local.TemperatureUnit
import java.util.Date

/**
 * Room TypeConverters — how to persist Kotlin types SQLite does not support.
 *
 * SQLite understands only a small set of storage classes: NULL, INTEGER, REAL,
 * TEXT and BLOB. Room maps primitives (Int, Long, Double, String) out of the
 * box, but it cannot store a [Date], a list or an enum by itself. A converter
 * teaches Room how to turn such a value into one of the supported types and
 * back:
 *
 *     Kotlin value  --(converter)-->  column value
 *     Date          --(converter)-->  Long (epoch milliseconds)
 *
 * Rules of thumb:
 *  - every pair of methods must be exact inverses of each other;
 *  - declare converters for nullable types as well (`String?`), otherwise Room
 *    fails to compile an entity that has a nullable column;
 *  - the class is registered once on the database:
 *    `@TypeConverters(Converters::class)` in [AppDatabase];
 *  - Room looks up a converter by parameter type, so method names are free.
 *
 * Keeping ALL converters in one place makes the storage format visible at a
 * glance: anyone reading the database sees `addedAt INTEGER` and can find out
 * here that it holds epoch milliseconds.
 */
class Converters {

    /* ------------------------------------------------------------------ *
     * Example 1: Date <-> Long
     *
     * Stored as epoch milliseconds (the same value System.currentTimeMillis()
     * returns), so ORDER BY and comparisons keep working in SQL.
     * ------------------------------------------------------------------ */

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun timestampToDate(epochMillis: Long?): Date? = epochMillis?.let(::Date)

    /* ------------------------------------------------------------------ *
     * Example 2: List<String> <-> String (JSON)
     *
     * SQL has no arrays, so a list is serialized into a TEXT column:
     *     listOf("action", "drama")  ->  ["action","drama"]
     *
     * Gson is already a project dependency (it parses the weather API), so we
     * reuse it instead of inventing a delimiter-separated format — JSON is
     * unambiguous and survives values that contain commas or quotes.
     *
     * NOTE: no entity stores a List<String> yet. The converter is ready for an
     * exercise (for example, adding movie genres or city tags).
     * ------------------------------------------------------------------ */

    @TypeConverter
    fun stringListToJson(list: List<String>?): String? = list?.let(gson::toJson)

    @TypeConverter
    fun jsonToStringList(json: String?): List<String>? =
        json?.let { gson.fromJson(it, stringListType) }

    /* ------------------------------------------------------------------ *
     * Example 3: enum <-> String
     *
     * An enum is stored by its constant name ("METRIC"), not by ordinal:
     *  - the name is readable in the database;
     *  - reordering or inserting constants does not silently change the
     *    meaning of already stored rows (which `ordinal` would do);
     *  - an unknown name is treated as null instead of crashing the app when
     *    data was written by an older/newer version.
     *
     * NOTE: no entity uses [TemperatureUnit] yet. It is ready for the
     * "measurement units" exercise from the README.
     * ------------------------------------------------------------------ */

    @TypeConverter
    fun temperatureUnitToString(unit: TemperatureUnit?): String? = unit?.name

    @TypeConverter
    fun stringToTemperatureUnit(name: String?): TemperatureUnit? =
        name?.let { stored -> TemperatureUnit.entries.firstOrNull { it.name == stored } }

    private companion object {
        /** Thread-safe, so a single instance is enough. */
        val gson = Gson()

        /** Gson needs the full generic type to build List<String>. */
        val stringListType = object : TypeToken<List<String>>() {}.type
    }
}
