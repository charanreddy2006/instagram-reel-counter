package com.brainrot.reeltracker.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MilestoneCalculatorTest {

    @Test
    fun testStageProgression() {
        assertEquals(MilestoneStage.FRESH, MilestoneCalculator.getCurrentStage(0))
        assertEquals(MilestoneStage.FRESH, MilestoneCalculator.getCurrentStage(25))
        assertEquals(MilestoneStage.GLAZED, MilestoneCalculator.getCurrentStage(26))
        assertEquals(MilestoneStage.GLAZED, MilestoneCalculator.getCurrentStage(50))
        assertEquals(MilestoneStage.MELTING, MilestoneCalculator.getCurrentStage(51))
        assertEquals(MilestoneStage.MELTING, MilestoneCalculator.getCurrentStage(99))
        assertEquals(MilestoneStage.BRAINROT, MilestoneCalculator.getCurrentStage(100))
        assertEquals(MilestoneStage.BRAINROT, MilestoneCalculator.getCurrentStage(150))
    }

    @Test
    fun testMilestoneAlerts() {
        assertNull("Count under 50 should have no alert", MilestoneCalculator.getMilestoneAlert(10))
        assertNull("Count 49 should have no alert", MilestoneCalculator.getMilestoneAlert(49))

        val alert50 = MilestoneCalculator.getMilestoneAlert(50)
        assertNotNull("Count 50 must trigger an alert", alert50)
        assertEquals(50, alert50?.milestoneCount)
        assertEquals(MilestoneStage.MELTING, alert50?.stage)
        assertEquals("50 Reels Down Today! 🫠", alert50?.headline)

        assertNull("Count 51 should not trigger an alert", MilestoneCalculator.getMilestoneAlert(51))
        assertNull("Count 99 should not trigger an alert", MilestoneCalculator.getMilestoneAlert(99))

        val alert100 = MilestoneCalculator.getMilestoneAlert(100)
        assertNotNull("Count 100 must trigger an alert", alert100)
        assertEquals(100, alert100?.milestoneCount)
        assertEquals(MilestoneStage.BRAINROT, alert100?.stage)

        val alert150 = MilestoneCalculator.getMilestoneAlert(150)
        assertNotNull("Count 150 must trigger repeat milestone alert", alert150)
        assertEquals(150, alert150?.milestoneCount)
        assertEquals(MilestoneStage.BRAINROT, alert150?.stage)

        val alert200 = MilestoneCalculator.getMilestoneAlert(200)
        assertNotNull("Count 200 must trigger repeat milestone alert", alert200)
        assertEquals(200, alert200?.milestoneCount)
    }
}
