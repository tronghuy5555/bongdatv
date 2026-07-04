---
name: bump-version
description: Bump the BongDaTV app version in app/build.gradle.kts without publishing. Use when the user says "bump version", "increment version", "prepare next version", or asks to update versionCode/versionName only.
---

# Bump Version

Update version numbers without doing a full release.

## Steps

1. Read the current version from `app/build.gradle.kts`:
   - `versionCode`
   - `versionName`

2. Determine the bump type. Ask whether it should be patch, minor, or major unless the user already specified:
   - Patch: `1.0.0` to `1.0.1` for bug fixes
   - Minor: `1.0.0` to `1.1.0` for new features
   - Major: `1.0.0` to `2.0.0` for breaking changes

3. Edit `app/build.gradle.kts`:
   - Increment `versionCode` by 1
   - Set `versionName` to the new semver string

4. Report the new version to the user. Do not commit unless asked.
