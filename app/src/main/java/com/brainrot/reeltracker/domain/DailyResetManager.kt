package com.brainrot.reeltracker.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DailyResetManager {
    private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE

    fun getTodayDateString(now: LocalDate = LocalDate.now()): String {
        return now.format(DATE_FORMATTER)
    }

    /**
     * Checks if a date rollover occurred.
     * Returns Pair(newTodayCount, updatedDateString).
     */
    fun evaluateReset(
        currentTodayCount: Int,
        lastActiveDate: String,
        todayDate: String
    ): Pair<Int, String> {
        return if (lastActiveDate != todayDate) {
            Pair(0, todayDate)
        } else {
            Pair(currentTodayCount, lastActiveDate)
        }
    }
}
