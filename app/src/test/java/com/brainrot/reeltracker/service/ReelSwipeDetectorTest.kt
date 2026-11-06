package com.brainrot.reeltracker.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReelSwipeDetectorTest {

    private lateinit var detector: ReelSwipeDetector

    @Before
    fun setUp() {
        detector = ReelSwipeDetector(debounceWindowMs = 800L)
    }

    @Test
    fun testIgnoresNonInstagramPackages() {
        assertFalse(detector.shouldCountSwipe(packageName = "com.android.chrome", eventTimestamp = 1000L))
        assertFalse(detector.shouldCountSwipe(packageName = "com.facebook.katana", eventTimestamp = 2000L))
        assertFalse(detector.shouldCountSwipe(packageName = null, eventTimestamp = 3000L))
    }

    @Test
    fun testAcceptsFirstValidInstagramSwipe() {
        assertTrue(detector.shouldCountSwipe(packageName = "com.instagram.android", eventTimestamp = 1000L))
    }

    @Test
    fun testDebouncesRapidFlickEvents() {
        // First swipe accepted
        assertTrue(detector.shouldCountSwipe("com.instagram.android", 1000L))

        // Subsequent rapid events within 800ms rejected
        assertFalse(detector.shouldCountSwipe("com.instagram.android", 1200L))
        assertFalse(detector.shouldCountSwipe("com.instagram.android", 1500L))
        assertFalse(detector.shouldCountSwipe("com.instagram.android", 1799L))

        // Event after 800ms accepted
        assertTrue(detector.shouldCountSwipe("com.instagram.android", 1801L))
    }
}
