# BongDaTV - Android TV App

## Project Overview
Android TV app for soccer/sports streaming from HoiQuanTV APIs.

## Tech Stack
- Kotlin 2.0.21, Jetpack Compose for TV (tv-compose 1.0.0)
- ExoPlayer (Media3 1.4.1) with HLS streaming
- Retrofit + kotlinx.serialization for API
- Hilt for DI, Coil for images
- Navigation Compose for routing
- AGP 8.7.3, compileSdk 35, minSdk 23, targetSdk 34

## Architecture
```
com.bongdatv/
├── data/
│   ├── api/          # Retrofit interfaces (HoiQuanApi, LiveStatsApi)
│   ├── model/        # Data classes (Fixture, Sport, League, LiveStats)
│   └── repository/   # SportRepository
├── di/               # Hilt NetworkModule
├── ui/
│   ├── components/   # Reusable: HeroBanner, MatchCard, MatchRow
│   ├── home/         # HomeScreen, HomeViewModel, SportTabRow
│   ├── player/       # PlayerScreen, PlayerViewModel (ExoPlayer)
│   ├── schedule/     # ScheduleScreen, ScheduleViewModel
│   ├── navigation/   # AppNavigation (NavHost)
│   └── theme/        # Colors, theme
└── update/           # UpdateChecker, UpdateDialog (GitHub Releases)
```

## Build & Run
```bash
./gradlew assembleDebug                    # Build debug APK
./gradlew installDebug                     # Install on connected device/emulator
adb shell am start -n com.bongdatv/.MainActivity  # Launch app
```

## Publishing a New Version

### Step-by-step release process:

1. **Update version** in `app/build.gradle.kts`:
   ```kotlin
   versionCode = <increment by 1>
   versionName = "<new semver>"  // e.g., "1.1.0"
   ```

2. **Update CHANGELOG.md** (see format below)

3. **Commit and tag**:
   ```bash
   git add -A
   git commit -m "release: v<version>"
   git tag v<version>
   git push origin main --tags
   ```

4. **Build the APK**:
   ```bash
   ./gradlew assembleDebug
   ```

5. **Create GitHub Release**:
   ```bash
   gh release create v<version> \
     app/build/outputs/apk/debug/app-debug.apk \
     --title "v<version>" \
     --notes-file CHANGELOG_LATEST.md \
     --repo tronghuy5555/bongdatv
   ```

   Or with inline notes:
   ```bash
   gh release create v<version> \
     app/build/outputs/apk/debug/app-debug.apk \
     --title "v<version>" \
     --notes "## What's new
   - Feature 1
   - Fix 1" \
     --repo tronghuy5555/bongdatv
   ```

### How auto-update works:
- On app launch, `UpdateChecker` calls GitHub API: `GET /repos/tronghuy5555/bongdatv/releases/latest`
- Compares `tag_name` (e.g., "v1.1.0") against `BuildConfig.VERSION_NAME`
- If newer version found with `.apk` asset attached, shows `UpdateDialog`
- User taps "Cập nhật" → downloads APK via DownloadManager → prompts install
- 24h dismiss cooldown if user taps "Để sau"

### Version naming:
- Format: `MAJOR.MINOR.PATCH` (semver)
- Tag format: `v1.0.0`, `v1.1.0`, `v2.0.0`
- `versionCode` must increment with every release (integer)

## Changelog Format

Use `CHANGELOG.md` at project root:
```markdown
# Changelog

## [1.1.0] - 2026-06-01
### Added
- New feature description

### Fixed
- Bug fix description

### Changed
- Change description

## [1.0.0] - 2026-05-31
### Added
- Initial release
```

## GitHub
- Repo: https://github.com/tronghuy5555/bongdatv
- Owner: tronghuy5555
- Auto-update config: `AppNavigation.kt` → `GITHUB_OWNER` / `GITHUB_REPO`

## API Endpoints
- HoiQuanTV: `https://sv.hoiquantv.xyz/api/v1/external/`
- LiveStats: `https://data.livestats.online/api/v1/`

## UI Language
Vietnamese only. All user-facing text is in Vietnamese.

## D-pad Navigation
All interactive elements use `focusable()` + `onFocusChanged` with green border highlight for focus state. No touch-specific interactions.
