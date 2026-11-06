package com.brainrot.reeltracker.service

class ReelSwipeDetector(private val debounceWindowMs: Long = 800L) {

    private var lastSwipeTimestamp: Long = 0L

    companion object {
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
    }

    @Synchronized
    fun shouldCountSwipe(packageName: String?, eventTimestamp: Long): Boolean {
        if (packageName != INSTAGRAM_PACKAGE) {
            return false
        }

        val elapsed = eventTimestamp - lastSwipeTimestamp
        if (elapsed in 0 until debounceWindowMs) {
            return false
        }

        lastSwipeTimestamp = eventTimestamp
        return true
    }

    @Synchronized
    fun reset() {
        lastSwipeTimestamp = 0L
    }
}
