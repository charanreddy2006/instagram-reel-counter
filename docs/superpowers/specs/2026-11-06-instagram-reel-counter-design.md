# Design Document: Instagram Reel Counter & Brainrot Milestone Alert (Android)

**Date:** 2026-11-06  
**Status:** Approved by User  
**Target Platform:** Native Android (Kotlin, Android SDK 26+)  

---

## 1. Intent and The Why

The user wants a 100% free, unlimited, and completely safe personal Android application that counts how many Instagram Reels they watch throughout each day (24-hour cycle). As reel consumption increases, an animated overlay appears at milestones (50, 100, 150+ reels) displaying an emoji face that deteriorates from a fresh, sharp expression into full "brainrot". The goal is to provide immediate, playful, yet impactful awareness of excessive scrolling without cluttering the screen or risking their Instagram account.

---

## 2. Goals, Non-Goals, and Anti-Goals

### Goals
- **100% Account Safety:** Zero Instagram credentials, zero private API calls, zero network interactions with Meta servers. Instagram cannot detect or ban the user for using this app.
- **100% Free & Unlimited:** Runs entirely on-device on Android. No servers, paid APIs, subscriptions, or 24/7 background machines required.
- **Stealth Experience:** Completely invisible during regular browsing. No floating bubbles or persistent overlays blocking the feed.
- **Buttery Smooth Performance:** Zero UI lag or gestures getting stuck. The overlay window uses hardware acceleration and lightweight native animations.
- **Accurate Reel Transition Detection:** Detect vertical swipes/reel view changes in Instagram's Reel player using Android's Accessibility Service with debounce logic.
- **24-Hour Cumulative Milestone Tracking:**
  - `0 – 25 Reels`: 😇 Fresh & Sharp
  - `26 – 50 Reels`: 🥱 Glazed Eyes
  - `51 – 99 Reels`: 🫠 Melting Brain (Alert fires at 50)
  - `100+ Reels`: 💀 Total Brainrot (Alert fires at 100, then repeats every 50 reels at 150, 200, 250...)
- **Automatic & Manual Resets:** Daily reset at midnight (24h period), plus a manual reset button on the app dashboard.

### Non-Goals
- Web browser extensions (user specified Android phone only).
- Scraping reel contents, captions, creators, audio, or metadata.
- Blocking or force-closing Instagram.
- Remote telemetry or cloud syncing.

### Anti-Goals
- **No Background Server Requirement:** Avoid any need for 24/7 cloud servers or keeping a laptop running.
- **No Intrusive or Unresponsive Popups:** The popup must never trap touch input or crash Instagram's gesture handling.
- **No Account Login Screens:** Never prompt the user for Instagram login/passwords.

---

## 3. Constraints & Permissions

- **Android Accessibility & Overlay Permissions:** Requires user to grant `BIND_ACCESSIBILITY_SERVICE` and `SYSTEM_ALERT_WINDOW` ("Display over other apps") once upon setup.
- **Instagram App Specificity:** Detection must only activate when foreground package is `com.instagram.android` and within the Reel container to prevent false positive count increments when scrolling regular feeds or stories.
- **Swipe Debouncing:** Instagram reels can produce multiple accessibility scroll events during a single swipe. The detection engine must debounce events (minimum 800ms threshold between counted transitions).

---

## 4. Approaches Considered

### Approach 1: Native Android App (Kotlin + AccessibilityService + WindowManager) — *Selected*
- **Description:** A dedicated, standalone Android app written in Kotlin. Employs `AccessibilityService` for package-scoped event filtering and `WindowManager` (`TYPE_APPLICATION_OVERLAY`) for rendering smooth animated alert views.
- **Pros:** 100% free forever, minimal battery consumption, native 60fps animations, completely private, builds into a standalone installable APK.
- **Cons:** Requires initial permission granting on Android settings.
- **Selection Reason:** Satisfies the user's hard requirements: "100% free", "100% safety", "smooth as butter", and "in my phone only".

### Approach 2: Macro Automation Profile (MacroDroid / Tasker)
- **Description:** Pre-configured automation macro scripts running inside MacroDroid/Tasker.
- **Rejected:** Fails the free standalone app and buttery-smooth criteria.

### Approach 3: Background Cloud / Server Bot with Instagram API
- **Description:** Running a script 24/7 on a server or laptop to track reel activity.
- **Rejected:** Strongly rejected due to Instagram account safety and ban risks.

---

## 5. System Architecture & Detailed Flow

```
+-----------------------------------------------------------+
| Android Operating System                                  |
|                                                           |
|   +-----------------------+     Accessibility Events      |
|   |  Instagram App        |  (com.instagram.android)      |
|   |  (Reels Player View)  | ------------------------+     |
|   +-----------------------+                         |     |
|                                                     v     |
|   +-----------------------------------------------------+ |
|   | ReelTrackerAccessibilityService                     | |
|   |   - Filter: com.instagram.android                   | |
|   |   - Debounce window: 800ms                          | |
|   |   - Reel container identifier check                 | |
|   |   - Increments todayCount (24-hour cumulative)      | |
|   |   - Triggers MilestoneOverlayManager at 50, 100,    | |
|   |     and every 50 reels thereafter (150, 200...)     | |
|   +-----------------------------------------------------+ |
|                             |                             |
|                             v                             |
|   +-----------------------------------------------------+ |
|   | MilestoneOverlayManager                             | |
|   |   - WindowManager.addView (TYPE_APPLICATION_OVERLAY)| |
|   |   - Animated Card: Scale/Fade In, Vibration Haptic  | |
|   |   - 3.5-second auto-dismiss with fade-out           | |
|   |   - Non-blocking touches (FLAG_NOT_FOCUSABLE)       | |
|   +-----------------------------------------------------+ |
|                             |                             |
|                             v                             |
|   +-----------------------------------------------------+ |
|   | Storage & Dashboard (MainActivity)                  | |
|   |   - SharedPreferences: todayCount, allTimeCount     | |
|   |   - Midnight Daily Reset (AlarmManager/Daily Check) | |
|   |   - Manual Reset Button                             | |
|   |   - Test Alert Preview Buttons (Test 50, Test 100)  | |
|   +-----------------------------------------------------+ |
+-----------------------------------------------------------+
```

### 5.1 Progression & Alert Details
- **0 – 25 reels:** `😇 Fresh & Sharp`
- **26 – 50 reels:** `🥱 Glazed Eyes`
- **50 reels (Milestone 1):** Alert card slides down with light vibration and message: *"50 Reels Down Today! 🫠 Your brain is starting to melt. Take a breather!"*
- **100 reels (Milestone 2):** Alert card flashes with distinct vibration buzz and message: *"100 REELS TODAY! 💀 Full Brainrot Mode Activated. Put the phone down!"*
- **150, 200, 250+ reels:** Continues firing every 50 reels with escalated copy and `💀` brainrot styling.

### 5.2 Overlay Behavior
- **Window Type:** `WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY`
- **Flags:** `FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_IN_SCREEN`
- **Duration:** 3.5 seconds total (0.3s slide-in, 2.9s display, 0.3s fade-out).
- **Dismissibility:** Auto-dismisses on timer or immediate dismiss on user tap.

---

## 6. What's Left to the Builder (Builder Calls Made)

1. **Post-100 Recurrence:** Milestone alert triggers every 50 reels (150, 200, 250...) with the `💀` Brainrot face.
2. **Debounce Threshold:** Set to 800ms between reel scroll events to prevent duplicate counting during fast gesture releases.
3. **Daily Reset Logic:** Implemented using date comparison on app wake/event (`lastResetDate != todayDate`) combined with an exact midnight `AlarmManager` trigger.
4. **Testing UI:** `MainActivity` includes "Simulate 50 Reel Alert" and "Simulate 100 Reel Alert" buttons so the user can test and enjoy the animations anytime without having to scroll 50 reels first.
