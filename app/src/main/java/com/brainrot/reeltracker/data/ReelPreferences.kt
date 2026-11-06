package com.brainrot.reeltracker.data

import android.content.Context
import android.content.SharedPreferences
import com.brainrot.reeltracker.domain.DailyResetManager

class ReelPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("reel_tracker_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TODAY_COUNT = "today_count"
        private const val KEY_ALL_TIME_COUNT = "all_time_count"
        private const val KEY_LAST_DATE = "last_active_date"
    }

    @Synchronized
    fun getTodayCount(): Int {
        checkDailyRollover()
        return prefs.getInt(KEY_TODAY_COUNT, 0)
    }

    @Synchronized
    fun getAllTimeCount(): Int {
        return prefs.getInt(KEY_ALL_TIME_COUNT, 0)
    }

    @Synchronized
    fun incrementCount(): Int {
        checkDailyRollover()
        val currentToday = prefs.getInt(KEY_TODAY_COUNT, 0) + 1
        val currentAllTime = prefs.getInt(KEY_ALL_TIME_COUNT, 0) + 1
        prefs.edit()
            .putInt(KEY_TODAY_COUNT, currentToday)
            .putInt(KEY_ALL_TIME_COUNT, currentAllTime)
            .apply()
        return currentToday
    }

    @Synchronized
    fun resetTodayCount() {
        prefs.edit()
            .putInt(KEY_TODAY_COUNT, 0)
            .putString(KEY_LAST_DATE, DailyResetManager.getTodayDateString())
            .apply()
    }

    private fun checkDailyRollover() {
        val todayStr = DailyResetManager.getTodayDateString()
        val storedDate = prefs.getString(KEY_LAST_DATE, "") ?: ""
        val currentToday = prefs.getInt(KEY_TODAY_COUNT, 0)

        val (newCount, newDate) = DailyResetManager.evaluateReset(currentToday, storedDate, todayStr)
        if (newDate != storedDate || newCount != currentToday) {
            prefs.edit()
                .putInt(KEY_TODAY_COUNT, newCount)
                .putString(KEY_LAST_DATE, newDate)
                .apply()
        }
    }
}
