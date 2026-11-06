package com.brainrot.reeltracker.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyResetManagerTest {

    @Test
    fun testSameDateRetainsCount() {
        val (count, date) = DailyResetManager.evaluateReset(
            currentTodayCount = 42,
            lastActiveDate = "2026-11-06",
            todayDate = "2026-11-06"
        )
        assertEquals(42, count)
        assertEquals("2026-11-06", date)
    }

    @Test
    fun testNewDayResetsCountToZero() {
        val (count, date) = DailyResetManager.evaluateReset(
            currentTodayCount = 88,
            lastActiveDate = "2026-11-05",
            todayDate = "2026-11-06"
        )
        assertEquals(0, count)
        assertEquals("2026-11-06", date)
    }

    @Test
    fun testEmptyInitialDateSetsTodayDate() {
        val (count, date) = DailyResetManager.evaluateReset(
            currentTodayCount = 0,
            lastActiveDate = "",
            todayDate = "2026-11-06"
        )
        assertEquals(0, count)
        assertEquals("2026-11-06", date)
    }
}
