package com.brainrot.reeltracker.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.brainrot.reeltracker.R
import com.brainrot.reeltracker.data.ReelPreferences
import com.brainrot.reeltracker.domain.MilestoneCalculator
import com.brainrot.reeltracker.overlay.MilestoneOverlayManager
import com.brainrot.reeltracker.service.ReelTrackerAccessibilityService

class MainActivity : AppCompatActivity() {

    private lateinit var preferences: ReelPreferences

    private lateinit var textCurrentEmoji: TextView
    private lateinit var textTodayCount: TextView
    private lateinit var textStageTitle: TextView
    private lateinit var textAllTimeCount: TextView
    private lateinit var btnPermAccessibility: Button
    private lateinit var btnPermOverlay: Button
    private lateinit var btnTest50: Button
    private lateinit var btnTest100: Button
    private lateinit var btnResetToday: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = ReelPreferences(this)

        initViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardUI()
        updatePermissionStates()
    }

    private fun initViews() {
        textCurrentEmoji = findViewById(R.id.textCurrentEmoji)
        textTodayCount = findViewById(R.id.textTodayCount)
        textStageTitle = findViewById(R.id.textStageTitle)
        textAllTimeCount = findViewById(R.id.textAllTimeCount)
        btnPermAccessibility = findViewById(R.id.btnPermAccessibility)
        btnPermOverlay = findViewById(R.id.btnPermOverlay)
        btnTest50 = findViewById(R.id.btnTest50)
        btnTest100 = findViewById(R.id.btnTest100)
        btnResetToday = findViewById(R.id.btnResetToday)
    }

    private fun setupListeners() {
        btnPermAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "Find 'Brainrot Reel Tracker' and turn it ON", Toast.LENGTH_LONG).show()
        }

        btnPermOverlay.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }

        btnTest50.setOnClickListener {
            val alert = MilestoneCalculator.getMilestoneAlert(50)
            if (alert != null) {
                MilestoneOverlayManager.showMilestone(this, alert)
            }
        }

        btnTest100.setOnClickListener {
            val alert = MilestoneCalculator.getMilestoneAlert(100)
            if (alert != null) {
                MilestoneOverlayManager.showMilestone(this, alert)
            }
        }

        btnResetToday.setOnClickListener {
            preferences.resetTodayCount()
            updateDashboardUI()
            Toast.makeText(this, "Today's reel count reset to 0", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDashboardUI() {
        val today = preferences.getTodayCount()
        val allTime = preferences.getAllTimeCount()
        val stage = MilestoneCalculator.getCurrentStage(today)

        textTodayCount.text = today.toString()
        textCurrentEmoji.text = stage.emoji
        textStageTitle.text = "${stage.title} (${stage.rangeDisplay})"
        textAllTimeCount.text = "All-time reels counted: $allTime"
    }

    private fun updatePermissionStates() {
        val isAccessibilityEnabled = isAccessibilityServiceEnabled(this, ReelTrackerAccessibilityService::class.java)
        if (isAccessibilityEnabled) {
            btnPermAccessibility.text = getString(R.string.status_granted)
            btnPermAccessibility.isEnabled = false
            btnPermAccessibility.setBackgroundColor(ContextCompat.getColor(this, R.color.success))
        } else {
            btnPermAccessibility.text = getString(R.string.status_missing)
            btnPermAccessibility.isEnabled = true
            btnPermAccessibility.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary))
        }

        val isOverlayEnabled = Settings.canDrawOverlays(this)
        if (isOverlayEnabled) {
            btnPermOverlay.text = getString(R.string.status_granted)
            btnPermOverlay.isEnabled = false
            btnPermOverlay.setBackgroundColor(ContextCompat.getColor(this, R.color.success))
        } else {
            btnPermOverlay.text = getString(R.string.status_missing)
            btnPermOverlay.isEnabled = true
            btnPermOverlay.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary))
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName == context.packageName &&
                enabledServiceInfo.name == serviceClass.name
            ) {
                return true
            }
        }
        return false
    }
}
