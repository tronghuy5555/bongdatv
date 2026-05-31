---
name: bump-version
description: Bump the app version (versionCode and versionName) without publishing. Use when the user says "bump version", "increment version", or "prepare next version".
---

# Bump Version

Update version numbers without doing a full release.

## Steps

1. **Read current version** from `app/build.gradle.kts` (versionCode and versionName).

2. **Determine bump type** — Ask if patch/minor/major unless specified:
   - Patch: `1.0.0` → `1.0.1` (bug fixes)
   - Minor: `1.0.0` → `1.1.0` (new features)
   - Major: `1.0.0` → `2.0.0` (breaking changes)

3. **Edit `app/build.gradle.kts`**:
   - `versionCode` += 1
   - `versionName` = new version string

4. **Report** the new version to the user. Do not commit unless asked.
