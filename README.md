# 📱 Brainrot Reel Tracker (Android)

> **A 100% free, unlimited, and completely safe personal Android application that counts your Instagram Reels and pops up deteriorating brainrot emoji alerts at milestones (50, 100, 150+ reels).**

---

## 🔒 100% Account Safety Guarantee

- **Zero Instagram Logins:** You never enter your Instagram username, password, or token.
- **Zero Network Calls:** The app never communicates with Instagram / Meta servers. It operates entirely offline on your phone.
- **Zero Ban Risk:** Meta cannot detect or flag your account because nothing leaves your device. Android's official Accessibility framework does the tracking locally.
- **No Servers Needed:** Runs entirely on your phone hardware. No monthly cloud bills, no laptop running 24/7.

---

## 🎭 Emoji Deterioration Stages

As you scroll through Instagram Reels during the day (24-hour cycle):

| Reel Range | Stage | Emoji | Experience |
| :--- | :--- | :---: | :--- |
| **0 – 25 reels** | Fresh & Sharp | 😇 | Clean focus, innocent browsing |
| **26 – 50 reels** | Glazed Eyes | 🥱 | Mild fatigue, subconscious scrolling |
| **50 reels (Milestone 1)** | Melting Brain | 🫠 | **Stealth Pop-Up:** *"50 Reels Down Today! 🫠 Your brain is starting to melt. Take a breather!"* |
| **51 – 99 reels** | Melting | 🫠 | Attention cooked, doomscrolling mode |
| **100 reels (Milestone 2)**| Total Brainrot | 💀 | **Stealth Pop-Up:** *"100 REELS TODAY! 💀 Full Brainrot Mode Activated. Put the phone down!"* |
| **150, 200, 250+ reels** | Escalated Brainrot | 💀 | Warning alert repeats every 50 reels |

> **Stealth Mode:** The overlay stays **completely invisible** during normal browsing so your screen is 100% clean. When an alert fires at 50 or 100, a sleek glassmorphic card slides down with haptic vibration, remains for 3.2 seconds without blocking your taps, and automatically glides away.

---

## 🚀 How to Build & Install Your Free APK

### Method 1: Automatic 2-Minute Free Cloud Build (GitHub Actions)
You don't need Android Studio or Java installed on your computer:
1. Create a free repository on [GitHub](https://github.com/new).
2. Push this folder to your repository:
   ```bash
   git remote add origin https://github.com/<your-username>/instagram-reel-counter.git
   git branch -M main
   git push -u origin main
   ```
3. Click the **Actions** tab on your GitHub repository.
4. The **Build Android APK** workflow will run automatically (~2 minutes) and build the APK.
5. Click the completed run, scroll down to **Artifacts**, and download `app-brainrot-tracker-debug`.
6. Transfer the `.apk` to your Android phone and tap to install!

### Method 2: Open in Android Studio
1. Open **Android Studio** and select **Open Project**.
2. Select this directory: `C:\Users\CHARAN REDDY\.gemini\antigravity\scratch\instagram-reel-counter`.
3. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. Android Studio will generate the APK ready to install via USB/ADB.

---

## ⚙️ Initial Setup on Your Phone

When you open **Brainrot Reel Tracker** for the first time:

1. **Enable Accessibility Service:**
   - Tap the **ENABLE** button next to *Accessibility Service*.
   - In Android Settings, find **Brainrot Reel Tracker** (under Installed Services / Accessibility) and toggle it **ON**.
2. **Enable Display Over Other Apps:**
   - Tap the **ENABLE** button next to *Display Over Other Apps*.
   - Toggle **Allow display over other apps** to ON.
3. **Test Alerts Anytime:**
   - Tap **Preview 50 Alert (🫠)** or **Preview 100 Alert (💀)** on the app dashboard to preview the animation and vibration without scrolling 50 reels!
4. **Enjoy Scrolling:** Open Instagram and scroll reels normally! The app takes care of the rest.

---

## 🛠️ Project Structure

```
instagram-reel-counter/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/brainrot/reeltracker/
│   │   │   ├── domain/
│   │   │   │   ├── MilestoneStage.kt        # Progression tiers & emojis
│   │   │   │   ├── MilestoneCalculator.kt   # Threshold calculations (50, 100, 150...)
│   │   │   │   └── DailyResetManager.kt     # Midnight 24h reset logic
│   │   │   ├── data/
│   │   │   │   └── ReelPreferences.kt       # Persistent local counters
│   │   │   ├── service/
│   │   │   │   ├── ReelSwipeDetector.kt     # 800ms debounce gesture engine
│   │   │   │   └── ReelTrackerAccessibilityService.kt # Foreground Instagram listener
│   │   │   ├── overlay/
│   │   │   │   └── MilestoneOverlayManager.kt # WindowManager animated popup with haptics
│   │   │   └── ui/
│   │   │       └── MainActivity.kt          # Stats dashboard & 1-tap permission setup
│   │   └── res/                             # Layouts, drawables, glassmorphism styles
│   └── src/test/                            # Complete unit test suite
├── .github/workflows/
│   └── build-apk.yml                        # Automated cloud APK builder
└── docs/superpowers/                        # Full specs & implementation plan
```
