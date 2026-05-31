# BongDa TV

Android TV app for live soccer/sports streaming.

## Features

- Live match streaming (HLS via ExoPlayer)
- Live score overlay during playback
- Sport category filtering (Football, Basketball, Billiards, etc.)
- Schedule screen with date-grouped fixtures
- Auto-update via GitHub Releases
- Full D-pad navigation with focus highlighting

## Screenshots

| Home | Player |
|------|--------|
| Sport tabs, hero banner, match rows | HLS stream with live score overlay |

## Build

```bash
# Debug (testing)
./gradlew assembleDebug

# Release (production)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

## Tech Stack

- Kotlin 2.0.21
- Jetpack Compose for TV (tv-compose 1.0.0)
- ExoPlayer (Media3 1.4.1) with HLS
- Retrofit + kotlinx.serialization
- Hilt for DI
- Coil for images
- Navigation Compose

## Architecture

```
com.bongdatv/
├── data/          # API interfaces, models, repository
├── di/            # Hilt NetworkModule
├── ui/
│   ├── components/  # HeroBanner, MatchCard, MatchRow
│   ├── home/        # HomeScreen + ViewModel
│   ├── player/      # PlayerScreen + ViewModel (ExoPlayer)
│   ├── schedule/    # ScheduleScreen + ViewModel
│   ├── navigation/  # NavHost + auto-update logic
│   └── theme/       # Colors, theme
└── update/        # UpdateChecker, UpdateDialog
```

## Auto-Update

The app checks GitHub Releases on launch. If a newer version with an `.apk` asset is found, it shows an update dialog and an upgrade icon in the top bar. Tapping either downloads and installs the update.

## Requirements

- Android TV device or emulator
- minSdk 23, targetSdk 34
- Internet connection for streaming and update checks

## License

Private project.
