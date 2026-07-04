# Changelog

## [1.3.1] - 2026-07-04
### Fixed
- Player detail overlay can be toggled on or off from the settings icon with a TV remote
- Player keeps the screen awake during long streams so Android TV does not return to the launcher while video is playing

## [1.3.0] - 2026-07-04
### Added
- Player loads match details by fixture ID and supports multiple stream options from fixture commentators
- Match detail overlay with loading, error, stream selection, and live score feedback
- GitHub Actions CI with unit tests for fixture parsing and update version comparison
- Project-local Codex workflows for versioning, changelog updates, emulator testing, and publishing

### Fixed
- Player playback works with the updated HoiQuanTV web source headers
- Player overlay auto-hides after playback starts and can be shown again with D-pad controls

### Changed
- Home and schedule navigation open player screens by fixture ID instead of passing stream URLs directly
- Player state handling now manages fixture loading, stream selection, and live updates more reliably

## [1.2.0] - 2026-06-01
### Fixed
- Auto-update now installs APK automatically after download completes
- Update dialog shows changelog content from the release

## [1.1.0] - 2026-05-31
### Added
- Upgrade icon in home top bar when newer version available
- App logo with adaptive icon (soccer ball + TV antenna)
- Release build with signing and ProGuard minification

### Fixed
- Back button from player now returns to home in single press
- Focus restores to hero banner when returning from player
- Update dialog traps focus and handles Back to dismiss
- D-pad click handling across all interactive components

## [1.0.0] - 2026-05-31
### Added
- Home screen with hero banner, sport category tabs, live/upcoming/finished match rows
- ExoPlayer HLS streaming with live score overlay
- Schedule screen with date-grouped fixtures
- Sport filtering (Football, Basketball, etc.)
- GitHub Releases auto-update system
- D-pad navigation with focus highlighting
