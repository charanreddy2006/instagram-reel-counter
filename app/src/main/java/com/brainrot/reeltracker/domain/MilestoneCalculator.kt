package com.brainrot.reeltracker.domain

data class MilestoneAlert(
    val milestoneCount: Int,
    val stage: MilestoneStage,
    val headline: String,
    val message: String
)

object MilestoneCalculator {
    fun getCurrentStage(count: Int): MilestoneStage {
        return when {
            count <= 25 -> MilestoneStage.FRESH
            count <= 50 -> MilestoneStage.GLAZED
            count <= 99 -> MilestoneStage.MELTING
            else -> MilestoneStage.BRAINROT
        }
    }

    fun getMilestoneAlert(count: Int): MilestoneAlert? {
        if (count < 50) return null

        return when {
            count == 50 -> MilestoneAlert(
                milestoneCount = 50,
                stage = MilestoneStage.MELTING,
                headline = "50 Reels Down Today! 🫠",
                message = "Your brain is starting to melt. Take a breather!"
            )
            count == 100 -> MilestoneAlert(
                milestoneCount = 100,
                stage = MilestoneStage.BRAINROT,
                headline = "100 REELS TODAY! 💀",
                message = "Full Brainrot Mode Activated. Put the phone down!"
            )
            count > 100 && (count % 50 == 0) -> MilestoneAlert(
                milestoneCount = count,
                stage = MilestoneStage.BRAINROT,
                headline = "$count REELS TODAY! 💀",
                message = "Critical Brainrot Level reached ($count reels)! Close Instagram now!"
            )
            else -> null
        }
    }
}
