package com.brainrot.reeltracker.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.brainrot.reeltracker.R
import com.brainrot.reeltracker.domain.MilestoneAlert

object MilestoneOverlayManager {

    private const val TAG = "MilestoneOverlayManager"
    private val mainHandler = Handler(Looper.getMainLooper())
    private var activeOverlayView: View? = null
    private var dismissRunnable: Runnable? = null

    @Synchronized
    fun showMilestone(context: Context, alert: MilestoneAlert) {
        if (!Settings.canDrawOverlays(context)) {
            Log.w(TAG, "Cannot draw overlay: SYSTEM_ALERT_WINDOW permission not granted")
            return
        }

        mainHandler.post {
            try {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

                // Remove any currently showing overlay before creating a new one
                removeCurrentOverlay(windowManager)

                val inflater = LayoutInflater.from(context)
                val overlayView = inflater.inflate(R.layout.overlay_milestone_card, null)

                val textEmoji = overlayView.findViewById<TextView>(R.id.textEmoji)
                val textHeadline = overlayView.findViewById<TextView>(R.id.textHeadline)
                val textMessage = overlayView.findViewById<TextView>(R.id.textMessage)

                textEmoji.text = alert.stage.emoji
                textHeadline.text = alert.headline
                textMessage.text = alert.message

                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
                ).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                }

                // Add to window
                windowManager.addView(overlayView, params)
                activeOverlayView = overlayView

                // Start entrance animation
                val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_down_fade_in)
                overlayView.startAnimation(slideIn)

                // Haptic feedback
                triggerHapticFeedback(context)

                // Tap to dismiss
                overlayView.setOnClickListener {
                    dismissOverlayWithAnimation(context, windowManager, overlayView)
                }

                // Auto dismiss after 3.2 seconds
                val autoDismiss = Runnable {
                    dismissOverlayWithAnimation(context, windowManager, overlayView)
                }
                dismissRunnable = autoDismiss
                mainHandler.postDelayed(autoDismiss, 3200L)

            } catch (e: Exception) {
                Log.e(TAG, "Error displaying milestone overlay: ${e.message}", e)
            }
        }
    }

    private fun dismissOverlayWithAnimation(
        context: Context,
        windowManager: WindowManager,
        viewToDismiss: View
    ) {
        dismissRunnable?.let { mainHandler.removeCallbacks(it) }
        dismissRunnable = null

        if (viewToDismiss != activeOverlayView) return

        try {
            val slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_up_fade_out)
            viewToDismiss.startAnimation(slideOut)

            mainHandler.postDelayed({
                removeCurrentOverlay(windowManager)
            }, 250L)
        } catch (e: Exception) {
            removeCurrentOverlay(windowManager)
        }
    }

    private fun removeCurrentOverlay(windowManager: WindowManager) {
        activeOverlayView?.let { view ->
            try {
                if (view.isAttachedToWindow) {
                    windowManager.removeViewImmediate(view)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Exception while removing overlay: ${e.message}")
            }
            activeOverlayView = null
        }
    }

    private fun triggerHapticFeedback(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 100, 80, 160)
                    val amplitudes = intArrayOf(0, 180, 0, 255)
                    it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(longArrayOf(0, 100, 80, 160), -1)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to trigger vibration: ${e.message}")
        }
    }
}
