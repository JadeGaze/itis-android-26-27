package com.itis.itisandroid.data.local

import com.itis.itisandroid.weather.data.local.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

/**
 * Host-side tests for [Converters].
 *
 * A converter is pure Kotlin, so it can be tested without an emulator:
 * it is enough to check that every pair of methods is a real round-trip
 * (value -> column -> value). If a round-trip breaks, the data in the
 * database is corrupted, and no UI test would catch that early.
 */
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `Date survives a round-trip through epoch milliseconds`() {
        val date = Date(1_700_000_000_000L)

        val stored = converters.dateToTimestamp(date)
        val restored = converters.timestampToDate(stored)

        assertEquals(1_700_000_000_000L, stored)
        assertEquals(date, restored)
    }

    @Test
    fun `list of strings survives a round-trip through JSON`() {
        val values = listOf("action", "drama", "sci-fi, \"space\"")

        val stored = converters.stringListToJson(values)
        val restored = converters.jsonToStringList(stored)

        assertEquals(values, restored)
    }

    @Test
    fun `enum survives a round-trip through its name`() {
        val stored = converters.temperatureUnitToString(TemperatureUnit.IMPERIAL)
        val restored = converters.stringToTemperatureUnit(stored)

        assertEquals("IMPERIAL", stored)
        assertEquals(TemperatureUnit.IMPERIAL, restored)
    }

    @Test
    fun `unknown enum name does not crash and becomes null`() {
        assertNull(converters.stringToTemperatureUnit("KELVIN"))
    }

    @Test
    fun `nullable values stay null`() {
        assertNull(converters.dateToTimestamp(null))
        assertNull(converters.timestampToDate(null))
        assertNull(converters.stringListToJson(null))
        assertNull(converters.jsonToStringList(null))
        assertNull(converters.temperatureUnitToString(null))
        assertNull(converters.stringToTemperatureUnit(null))
    }
}
