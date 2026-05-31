# BongDaTV - Android TV App Design Spec

## Overview

An Android TV app for live sports streaming, consuming APIs from the HoiQuanTV platform. The app displays live/upcoming/finished matches across multiple sports, streams HLS video with commentator audio, and includes a self-update mechanism via GitHub Releases.

**Language:** Vietnamese only  
**Target:** Android TV (API 21+, Leanback)  
**Tech Stack:** Kotlin, Jetpack Compose for TV, ExoPlayer, Retrofit, Hilt, Coil

---

## API Integration

### Base URLs

| Service | URL | Purpose |
|---------|-----|---------|
| Main API | `https://sv.hoiquantv.xyz/api/v1/external/` | Sports, leagues, fixtures, replays |
| Live Stats | `https://data.livestats.online/api/v1/` | Real-time scores & match status |
| Streams | `https://hqlive.yarncdn.live/live/{username}/playlist.m3u8` | HLS video streams |

### Endpoints

| Endpoint | Method | Response |
|----------|--------|----------|
| `/sports` | GET | List of sports with icon, background, fixture counts, hasLive flag |
| `/leagues` | GET | All leagues grouped by sport |
| `/fixtures/unfinished` | GET | Live and upcoming matches with teams, scores, commentators, stream URLs |
| `/fixtures/finished` | GET | Completed matches with final scores |
| `/replays` | GET | Available match replays |
| Live Stats: `/fixtures?by=ids&value={ids}` | GET | Real-time fixture status, elapsed time, goals |

### Key Data Model (from API)

**Fixture:**
```
id, referenceId, slug, title, startTime, elapsedTime, isLive, isPinned, isHot
sport: { id, slug, name, iconUrl, backgroundCardUrl, backgroundMainUrl }
league: { id, slug, name, logoUrl }
homeTeam: { id, slug, name, logoUrl }
awayTeam: { id, slug, name, logoUrl }
score: { home, away }
status: { code, description } // FT, 1H, 2H, HT, etc.
fixtureCommentators: [
  {
    commentator: {
      name, nickname, avatarUrl,
      streams: [
        { name: "FHD", sourceUrl: "...playlist.m3u8" },
        { name: "HD", sourceUrl: "...tracks-v2a1/mono.m3u8" },
        { name: "SD", sourceUrl: "...tracks-v1a1/mono.m3u8" }
      ]
    }
  }
]
```

**Sport:**
```
id, slug, name, iconUrl, backgroundCardUrl, backgroundMainUrl, posterUrl
fixtureCount, liveFixtureCount, hasLive
```

---

## Screens

### 1. Home Screen (Main)

Layout inspired by VNPT Sports TV reference:
- **Top bar:** App logo + search icon (focusable)
- **Hero banner:** Currently live/hot match with large background image, match title, score, teams
- **Horizontal rows:**
  - "Đang phát trực tiếp" (Live now) — matches where `isLive: true`
  - "Sắp diễn ra" (Upcoming) — from unfinished fixtures, not yet live
  - "Kết quả" (Results) — from finished fixtures
- Each card shows: team logos, score, league name, time/status, commentator name

**Focus behavior:** D-pad left/right scrolls within a row, up/down moves between rows. Hero banner is focusable and selectable.

### 2. Player Screen

- **ExoPlayer** fullscreen with HLS playback
- **Quality selector overlay:** FHD / HD / SD (maps to commentator stream URLs)
- **Score overlay** (toggle with select button): live score from livestats API, updates every 30s
- **Commentator info:** name and avatar in corner
- **Controls:** Play/pause, back to exit

### 3. Schedule Screen (Lịch thi đấu)

- Vertical list of matches grouped by date
- Each match card: teams, time, league, live indicator
- Filter tabs at top: by sport category

### 4. Sport Categories

- Left navigation rail: Bóng đá, Cầu lông, Tennis, Bóng chuyền, Billiards, Esports, Bóng rổ, Đua xe, Boxing, Bóng bàn
- Selecting a sport filters all content to that sport
- Shows live count badge on sports with active streams

### 5. Settings Screen

- App version info
- Check for updates manually
- Stream quality default (FHD/HD/SD)
- About

---

## App Upgrade System

**Mechanism:** GitHub Releases API

1. On app launch, call `https://api.github.com/repos/{owner}/{repo}/releases/latest`
2. Parse `tag_name` as version, compare with current `BuildConfig.VERSION_NAME`
3. If newer version available:
   - Show non-blocking dialog: "Có phiên bản mới {version}. Cập nhật ngay?"
   - User selects "Cập nhật" → download APK from release assets
   - Use `DownloadManager` to fetch APK
   - Prompt install via `ACTION_INSTALL_PACKAGE` intent
4. If user dismisses, don't prompt again for 24 hours

---

## Navigation & Remote Control

All navigation uses D-pad (TV remote):
- **D-pad Up/Down:** Move between rows or vertical items
- **D-pad Left/Right:** Scroll within horizontal rows
- **Select (OK):** Open match detail / start stream / confirm actions
- **Back:** Return to previous screen, exit from home
- **Menu:** Open settings (long-press or dedicated button)

Focus states:
- Cards get a highlighted border + slight scale-up on focus
- Current focused item always visible (auto-scroll into view)
- Smooth animations on focus transitions

---

## Architecture

```
app/
├── data/
│   ├── api/          # Retrofit interfaces
│   ├── model/        # API response data classes
│   └── repository/   # Repository implementations
├── di/               # Hilt modules
├── ui/
│   ├── home/         # Home screen composables + ViewModel
│   ├── player/       # Player screen with ExoPlayer
│   ├── schedule/     # Schedule screen
│   ├── settings/     # Settings screen
│   └── components/   # Shared TV components (cards, rows, focus)
├── update/           # GitHub release update checker
└── util/             # Extensions, constants
```

**Key Dependencies:**
- `androidx.tv:tv-compose` — Compose for TV components
- `androidx.media3:media3-exoplayer` — ExoPlayer with HLS
- `com.squareup.retrofit2:retrofit` — HTTP client
- `com.google.dagger:hilt-android` — DI
- `io.coil-kt:coil-compose` — Image loading
- `kotlinx.serialization` — JSON parsing

**Data Flow:**
```
API → Retrofit → Repository → ViewModel (StateFlow) → Compose UI
                                    ↓
              LiveStats polling (30s interval for live matches)
                                    ↓
              ExoPlayer ← m3u8 URL from selected commentator stream
```

---

## Non-Functional Requirements

- App launch to content visible: < 2 seconds
- Stream start latency: < 3 seconds
- Minimum supported Android TV: API 21 (Lollipop)
- APK size target: < 15MB
- Handles network errors gracefully with retry UI
- Supports both 1080p and 4K TV displays
