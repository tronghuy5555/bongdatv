# BongDaTV Implementation Plan

> **For agentic workers:** Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a working Android TV app that streams live sports from HoiQuanTV APIs.

**Approach:** Each task produces a runnable app. Start with skeleton → API → UI → Player → Upgrade.

---

## Task 1: Android TV Project Scaffold

Create a minimal Android TV project that compiles and shows a blank screen.

**Files to create:**
- `build.gradle.kts` (project-level)
- `settings.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml` (version catalog)
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/bongdatv/App.kt` (Hilt Application)
- `app/src/main/java/com/bongdatv/MainActivity.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`

**Key decisions:**
- minSdk 21, targetSdk 34
- Kotlin 2.0+, Compose BOM, TV Compose, Media3, Retrofit, Hilt, Coil, kotlinx.serialization
- Single Activity architecture with Compose navigation

**Done when:** `./gradlew assembleDebug` succeeds.

---

## Task 2: Data Layer — API Models + Retrofit

Define all API response models and Retrofit service interfaces.

**Files to create:**
- `app/src/main/java/com/bongdatv/data/model/Sport.kt`
- `app/src/main/java/com/bongdatv/data/model/League.kt`
- `app/src/main/java/com/bongdatv/data/model/Fixture.kt`
- `app/src/main/java/com/bongdatv/data/model/LiveStats.kt`
- `app/src/main/java/com/bongdatv/data/api/HoiQuanApi.kt`
- `app/src/main/java/com/bongdatv/data/api/LiveStatsApi.kt`
- `app/src/main/java/com/bongdatv/data/repository/SportRepository.kt`
- `app/src/main/java/com/bongdatv/di/NetworkModule.kt`

**API base URLs:**
- Main: `https://sv.hoiquantv.xyz/api/v1/external/`
- LiveStats: `https://data.livestats.online/api/v1/`

**Done when:** App compiles with all models and API interfaces defined.

---

## Task 3: Home Screen — Hero Banner + Match Rows

Build the home screen UI with real API data.

**Files to create:**
- `app/src/main/java/com/bongdatv/ui/home/HomeScreen.kt`
- `app/src/main/java/com/bongdatv/ui/home/HomeViewModel.kt`
- `app/src/main/java/com/bongdatv/ui/components/MatchCard.kt`
- `app/src/main/java/com/bongdatv/ui/components/HeroBanner.kt`
- `app/src/main/java/com/bongdatv/ui/components/MatchRow.kt`
- `app/src/main/java/com/bongdatv/ui/theme/Theme.kt`

**Layout:**
- Top: Hero banner with live/hot match background + score
- Row 1: "Đang phát trực tiếp" — live matches
- Row 2: "Sắp diễn ra" — upcoming matches
- Row 3: "Kết quả" — finished matches
- D-pad focus navigation between rows/cards

**Done when:** App launches on Android TV emulator, shows real match data from API with focusable cards.

---

## Task 4: Video Player with ExoPlayer HLS

Clicking a match card opens fullscreen video player.

**Files to create:**
- `app/src/main/java/com/bongdatv/ui/player/PlayerScreen.kt`
- `app/src/main/java/com/bongdatv/ui/player/PlayerViewModel.kt`
- `app/src/main/java/com/bongdatv/ui/navigation/AppNavigation.kt`

**Features:**
- ExoPlayer with HLS (m3u8) playback
- Quality selector: FHD / HD / SD from commentator streams
- Score overlay with live updates (poll livestats every 30s)
- Back button returns to home

**Done when:** Selecting a live match plays the HLS stream fullscreen with quality options.

---

## Task 5: Sport Categories + Schedule

Add sport filtering and schedule view.

**Files to create:**
- `app/src/main/java/com/bongdatv/ui/home/SportTabRow.kt`
- `app/src/main/java/com/bongdatv/ui/schedule/ScheduleScreen.kt`
- `app/src/main/java/com/bongdatv/ui/schedule/ScheduleViewModel.kt`

**Features:**
- Sport tab row at top of home: Bóng đá, Cầu lông, Tennis, etc.
- Selecting a sport filters matches
- Schedule screen: matches grouped by date
- Navigation between Home ↔ Schedule via left rail or top nav

**Done when:** Can filter by sport and view schedule screen.

---

## Task 6: App Auto-Update via GitHub Releases

**Files to create:**
- `app/src/main/java/com/bongdatv/update/UpdateChecker.kt`
- `app/src/main/java/com/bongdatv/update/UpdateDialog.kt`

**Flow:**
1. On launch, GET `https://api.github.com/repos/{owner}/{repo}/releases/latest`
2. Compare `tag_name` with `BuildConfig.VERSION_NAME`
3. If newer → show dialog → download APK → prompt install
4. Dismiss = don't ask again for 24h (SharedPreferences)

**Done when:** App checks GitHub for updates and can download + install new APK.

---

## Execution Order

Each task builds on the previous. After each task the app is runnable:
1. ✅ Empty app that compiles
2. ✅ App with API layer (no visible change, but data flows)
3. ✅ App shows matches on home screen
4. ✅ App plays live streams
5. ✅ App has sport filtering + schedule
6. ✅ App self-updates
