package com.brainrot.reeltracker.domain

enum class MilestoneStage(
    val emoji: String,
    val title: String,
    val description: String,
    val rangeDisplay: String
) {
    FRESH("😇", "Fresh & Sharp", "Innocent scrolling, clear mind", "0 – 25 reels"),
    GLAZED("🥱", "Glazed Eyes", "Mild fatigue, doomscrolling starts", "26 – 50 reels"),
    MELTING("🫠", "Melting Brain", "50-reel milestone reached! Attention cooked", "51 – 99 reels"),
    BRAINROT("💀", "Total Brainrot", "Zombie mode, critical overscroll!", "100+ reels")
}
