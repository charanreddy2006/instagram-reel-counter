package com.brainrot.reeltracker.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.brainrot.reeltracker.data.ReelPreferences
import com.brainrot.reeltracker.domain.MilestoneCalculator
import com.brainrot.reeltracker.overlay.MilestoneOverlayManager

class ReelTrackerAccessibilityService : AccessibilityService() {

    private lateinit var preferences: ReelPreferences
    private val swipeDetector = ReelSwipeDetector(debounceWindowMs = 800L)

    companion object {
        private const val TAG = "ReelTrackerService"
        var isServiceRunning = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        preferences = ReelPreferences(applicationContext)
        isServiceRunning = true
        Log.i(TAG, "ReelTrackerAccessibilityService created")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val pkgName = event.packageName?.toString()
        if (pkgName != ReelSwipeDetector.INSTAGRAM_PACKAGE) return

        // Filter scroll events that indicate vertical swiping between reels
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {
            val eventTime = event.eventTime
            val effectiveTime = if (eventTime > 0) eventTime else System.currentTimeMillis()

            if (swipeDetector.shouldCountSwipe(pkgName, effectiveTime)) {
                val newCount = preferences.incrementCount()
                Log.d(TAG, "Reel swipe registered. Today's count: $newCount")

                val alert = MilestoneCalculator.getMilestoneAlert(newCount)
                if (alert != null) {
                    Log.i(TAG, "Milestone hit: ${alert.milestoneCount}! Triggering overlay.")
                    MilestoneOverlayManager.showMilestone(applicationContext, alert)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "ReelTrackerAccessibilityService interrupted")
        swipeDetector.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        Log.i(TAG, "ReelTrackerAccessibilityService destroyed")
    }
}
