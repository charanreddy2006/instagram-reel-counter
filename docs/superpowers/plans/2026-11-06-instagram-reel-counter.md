# Instagram Reel Counter & Brainrot Milestone Alert Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a native Android application that silently tracks Instagram Reel swipes on-device, maintains a 24-hour reel count, and displays animated overlay milestone alerts (with deteriorating emoji faces at 50, 100, 150+ reels) without any cloud servers or account credentials.

**Architecture:** A native Android app structured into three layers: an accessibility layer (`ReelTrackerAccessibilityService`) that filters and debounces Instagram reel transitions; a domain & persistence layer (`ReelCountManager` and `MilestoneCalculator`) managing daily cumulative stats and midnight resets; and a presentation layer (`MilestoneOverlayManager` and `MainActivity`) displaying hardware-accelerated animated overlay cards over Instagram alongside a permission/test dashboard.

**Tech Stack:** Kotlin, Android SDK (minSdk 26, targetSdk 34), Android View System (`WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`), Android Jetpack (ConstraintLayout, Material Components, JUnit 4, Roboelectric/Kotlin test).

**Spec:** [docs/superpowers/specs/2026-11-06-instagram-reel-counter-design.md](file:///C:/Users/CHARAN%20REDDY/.gemini/antigravity/scratch/instagram-reel-counter/docs/superpowers/specs/2026-11-06-instagram-reel-counter-design.md)

---

## Global Constraints

- **Platform:** Native Android (Kotlin, Android Gradle Plugin 8.2+, minSdk 26, targetSdk 34).
- **Zero Instagram Credentials:** No login forms, no Meta Graph API, no network requests to Instagram servers.
- **100% Free & Local:** Runs completely on-device with zero server or hosting dependencies.
- **Milestone Thresholds & Copy:**
  - 50 Reels: `🫠 Melting Brain` (*"50 Reels Down Today! 🫠 Your brain is starting to melt. Take a breather!"*)
  - 100 Reels: `💀 Total Brainrot` (*"100 REELS TODAY! 💀 Full Brainrot Mode Activated. Put the phone down!"*)
  - Post-100: Fires every 50 reels (150, 200, 250...) with `💀` brainrot styling.
- **Overlay Behavior:** `TYPE_APPLICATION_OVERLAY`, `FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL`, 3.5s duration with slide/fade animation.
- **Debounce Window:** 800ms minimum threshold between reel scroll events.

---

## Review Focus

1. **Rapid Swipe Flicks:** A user rapidly swiping back and forth should only count unique downwards/next advances once every 800ms, not increment 3-4 times.
2. **Midnight Date Rollover Mid-Session:** Scrolling past 00:00 local time must immediately rollover `todayCount` to 0 without requiring an app reboot or corrupting all-time stats.
3. **Overlay Touch Pass-Through:** When the milestone alert pops up, the user must still be able to tap/scroll Instagram beneath or dismiss the alert by tapping it, without touch locks.
4. **App Switch / Screen Off:** Turning off the screen or switching to another app must not trigger false positive reel counts or crash the accessibility service.
5. **Cold Reboot / Service Resumption:** After device reboot, accessibility service must gracefully restore its daily counter and state from persistent preferences.

---

## Tasks

### Task 1: Project Scaffolding & Android Gradle Build Setup

**Files:**
- Create: `build.gradle.kts` (Root project build file)
- Create: `settings.gradle.kts`
- Create: `gradle.properties`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/colors.xml`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/res/xml/accessibility_service_config.xml`

**Interfaces:**
- Produces: Complete Gradle project structure ready for build, with `com.brainrot.reeltracker` package, required permissions (`SYSTEM_ALERT_WINDOW`, `BIND_ACCESSIBILITY_SERVICE`, `VIBRATE`), and service declarations.

- [ ] **Step 1: Create root build script and Gradle settings**
  Define Android Gradle Plugin 8.2.2, Kotlin 1.9.22, repositories (google, mavenCentral), and app module inclusion.

- [ ] **Step 2: Create `app/build.gradle.kts`**
  Configure applicationId `com.brainrot.reeltracker`, minSdk 26, targetSdk 34, versionCode 1, versionName "1.0", dependencies (androidx.core:core-ktx, androidx.appcompat:appcompat, material:material, androidx.constraintlayout:constraintlayout, junit:junit).

- [ ] **Step 3: Create `AndroidManifest.xml` and accessibility config**
  Register `MainActivity`, `ReelTrackerAccessibilityService` with `android.permission.BIND_ACCESSIBILITY_SERVICE`, and `accessibility_service_config.xml` targeting `com.instagram.android` with `eventTypes="typeViewScrolled|typeWindowStateChanged|typeWindowContentChanged"`.

- [ ] **Step 4: Verify project structure integrity**
  Ensure XML attributes, themes, colors, and string resources resolve cleanly without missing references.

---

### Task 2: Core Domain Logic & State Management

**Files:**
- Create: `app/src/main/java/com/brainrot/reeltracker/domain/MilestoneStage.kt`
- Create: `app/src/main/java/com/brainrot/reeltracker/domain/MilestoneCalculator.kt`
- Create: `app/src/main/java/com/brainrot/reeltracker/domain/DailyResetManager.kt`
- Create: `app/src/main/java/com/brainrot/reeltracker/data/ReelPreferences.kt`
- Create: `app/src/test/java/com/brainrot/reeltracker/domain/MilestoneCalculatorTest.kt`
- Create: `app/src/test/java/com/brainrot/reeltracker/domain/DailyResetManagerTest.kt`

**Interfaces:**
- Produces:
  - `MilestoneCalculator.getMilestoneAlert(count: Int): MilestoneAlert?`
  - `MilestoneCalculator.getCurrentStage(count: Int): MilestoneStage`
  - `DailyResetManager.checkAndReset(currentDateStr: String, storedDateStr: String, currentCount: Int): Pair<Int, String>`
  - `ReelPreferences` wrapper for SharedPreferences persistence.

- [ ] **Step 1: Write failing unit test for `MilestoneCalculator`**
  Assert count 0-25 -> FRESH (`😇`), 26-50 -> GLAZED (`🥱`), exactly 50 -> MELTING alert (`🫠`), 51-99 -> MELTING, exactly 100 -> BRAINROT alert (`💀`), 150 -> BRAINROT alert (`💀`), non-milestone (51, 101, 149) -> null alert.

- [ ] **Step 2: Implement `MilestoneStage` and `MilestoneCalculator`**
  Implement the enum stages and pure milestone calculation function.

- [ ] **Step 3: Write failing unit test for `DailyResetManager`**
  Assert that when `todayDate != storedDate`, `todayCount` resets to 0 and `storedDate` updates to `todayDate`. When dates match, count remains unchanged.

- [ ] **Step 4: Implement `DailyResetManager` and `ReelPreferences`**
  Implement date check logic using ISO date format `YYYY-MM-DD` and thread-safe preference access.

- [ ] **Step 5: Run unit tests to verify they pass**
  Verify all assertions pass for milestone detection, repeat intervals (150, 200, 250...), and daily reset transitions.

---

### Task 3: Accessibility Reel Tracking Service

**Files:**
- Create: `app/src/main/java/com/brainrot/reeltracker/service/ReelTrackerAccessibilityService.kt`
- Create: `app/src/main/java/com/brainrot/reeltracker/service/ReelSwipeDetector.kt`
- Create: `app/src/test/java/com/brainrot/reeltracker/service/ReelSwipeDetectorTest.kt`

**Interfaces:**
- Consumes: `ReelPreferences`, `MilestoneCalculator`, `DailyResetManager`
- Produces: `ReelTrackerAccessibilityService` receiving AccessibilityEvents, debouncing swipes, incrementing count, and triggering overlay callbacks.

- [ ] **Step 1: Write unit test for `ReelSwipeDetector` debounce logic**
  Simulate rapid scroll event timestamps: event at t=0ms accepted; event at t=300ms rejected; event at t=850ms accepted. Verify only 2 counts triggered.

- [ ] **Step 2: Implement `ReelSwipeDetector`**
  Implement time-based debounce logic (800ms) and package validation (`com.instagram.android`).

- [ ] **Step 3: Implement `ReelTrackerAccessibilityService`**
  Override `onAccessibilityEvent`:
  - Check package name equals `com.instagram.android`.
  - Filter for scroll / reel layout transitions.
  - Call `ReelSwipeDetector.registerSwipe(eventTime)`.
  - On valid swipe: perform daily reset check, increment `todayCount`, increment `allTimeCount`, persist to `ReelPreferences`, check if milestone alert triggers, and invoke `MilestoneOverlayManager`.

---

### Task 4: Floating Hardware-Accelerated Milestone Overlay

**Files:**
- Create: `app/src/main/res/layout/overlay_milestone_card.xml`
- Create: `app/src/main/res/drawable/bg_milestone_card.xml`
- Create: `app/src/main/res/anim/slide_down_fade_in.xml`
- Create: `app/src/main/res/anim/slide_up_fade_out.xml`
- Create: `app/src/main/java/com/brainrot/reeltracker/overlay/MilestoneOverlayManager.kt`

**Interfaces:**
- Consumes: `MilestoneAlert` (emoji, title, message, milestoneNumber)
- Produces: `MilestoneOverlayManager.showMilestone(context: Context, alert: MilestoneAlert)`

- [ ] **Step 1: Design `overlay_milestone_card.xml` layout and drawables**
  Rounded corner glassmorphism card with dark backdrop, emoji badge (`android:textSize="48sp"`), bold title, reel counter pill, and dismissal caption.

- [ ] **Step 2: Implement `MilestoneOverlayManager`**
  - Verify `Settings.canDrawOverlays(context)`.
  - Build `WindowManager.LayoutParams`:
    - `width = MATCH_PARENT` (with horizontal margins)
    - `height = WRAP_CONTENT`
    - `type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`
    - `flags = FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_IN_SCREEN`
    - `gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL`
  - Animate entrance using `ObjectAnimator` (translationY from -150f to 0f, alpha 0f to 1f over 300ms).
  - Trigger haptic feedback (`Vibrator` with double buzz pattern `0, 100, 80, 150`).
  - Set a 3.2-second Handler delay to smoothly animate exit and remove view from `WindowManager`.
  - Allow immediate dismiss on tap without blocking touch pass-through.

---

### Task 5: Dashboard Activity & Permission Onboarding

**Files:**
- Create: `app/src/main/res/layout/activity_main.xml`
- Create: `app/src/main/res/drawable/bg_dashboard_card.xml`
- Create: `app/src/main/java/com/brainrot/reeltracker/ui/MainActivity.kt`

**Interfaces:**
- Consumes: `ReelPreferences`, `MilestoneOverlayManager`, `MilestoneCalculator`
- Produces: Launchable app UI allowing permission granting, live stats viewing, testing alert animations, and resetting counters.

- [ ] **Step 1: Design `activity_main.xml`**
  - Header with app title and dynamic brainrot status badge.
  - Permission status cards: "Accessibility Service" (Green if enabled, Red button to open Settings if disabled) and "Display Over Other Apps" (Green/Red with 1-tap grant button).
  - Reel Counter card displaying "Today's Reels", "All-Time Reels", and current emoji face.
  - Interactive Action buttons: "Simulate 50 Reel Alert", "Simulate 100 Reel Alert", and "Reset Today's Count".

- [ ] **Step 2: Implement `MainActivity.kt`**
  - Check `isAccessibilityServiceEnabled()` and `Settings.canDrawOverlays()`.
  - Add click listeners to launch `Settings.ACTION_ACCESSIBILITY_SETTINGS` and `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`.
  - Wire simulation buttons directly to `MilestoneOverlayManager.showMilestone(...)` to let the user immediately preview the experience.
  - Implement reset confirmation dialog and update UI in `onResume()`.

---

### Task 6: GitHub Actions Workflow, Build Scripts & Verification

**Files:**
- Create: `.github/workflows/build-apk.yml`
- Create: `scripts/build.ps1`
- Create: `README.md` (Clear setup & installation guide)

**Interfaces:**
- Produces: Automated CI/CD script that compiles the debug APK for free on GitHub or locally, and complete step-by-step documentation for installing and enabling it on the phone.

- [ ] **Step 1: Create GitHub Actions build workflow (`.github/workflows/build-apk.yml`)**
  Configure workflow on push/manual dispatch: sets up Java 17, runs Gradle build `./gradlew assembleDebug`, runs unit tests, and uploads `app-debug.apk` as a downloadable artifact.

- [ ] **Step 2: Create PowerShell local build script (`scripts/build.ps1`)**
  Checks Java/Android SDK environment, runs unit tests, and builds APK if Gradle/JDK is present.

- [ ] **Step 3: Create comprehensive `README.md`**
  Document permissions guide, feature explanation, simulation testing, and APK installation steps.

---

## Plan Self-Review Checklist

1. **Spec Coverage:**
   - 100% free and local? Covered in Tasks 1-6.
   - 100% account safety (zero login/API)? Covered in Tasks 1-3.
   - 24-hour cumulative count & midnight reset? Covered in Task 2.
   - Stealth overlay with deteriorating emojis (50, 100, 150+)? Covered in Tasks 2 & 4.
   - Buttery smooth, non-blocking touches? Covered in Task 4.
2. **Review Focus Items:**
   - Debounce rapid flicks? Covered in Task 3.
   - Midnight date rollover? Covered in Task 2.
   - Overlay touch pass-through? Covered in Task 4.
   - App switch safety? Covered in Task 3.
3. **Execution Readiness:**
   - Every task has exact file paths, interfaces, and step-by-step verify points.
