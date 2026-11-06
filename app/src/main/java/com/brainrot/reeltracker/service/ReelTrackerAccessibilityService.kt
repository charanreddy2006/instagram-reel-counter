package com.brainrot.reeltracker.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.SystemClock
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

        // Only count genuine scroll events; exclude window content changes, video playback progress, and UI redraws
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            val eventTime = event.eventTime
            val effectiveTime = if (eventTime > 0) eventTime else SystemClock.uptimeMillis()

            // Verify downward / forward progression (scrollDeltaY >= 0 or advancing item index)
            val isForward = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                event.scrollDeltaY >= 0
            } else {
                event.toIndex >= event.fromIndex
            }

            if (swipeDetector.shouldCountSwipe(pkgName, effectiveTime, isForward)) {
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
