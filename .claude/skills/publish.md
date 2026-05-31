---
name: publish
description: Build APK, bump version, update changelog, create git tag, and publish a GitHub Release with the APK attached. Use when the user says "publish", "release", "ship", or "new version".
---

# Publish Release

Automate the full release process for BongDaTV.

## Steps

1. **Ask what changed** — Get a brief description of changes for the changelog entry. If the user already described it, use that.

2. **Determine version bump** — Ask if this is a patch (bug fix), minor (new feature), or major (breaking change). Default to minor if unclear.

3. **Bump version** in `app/build.gradle.kts`:
   - Increment `versionCode` by 1
   - Update `versionName` to new semver

4. **Update `CHANGELOG.md`** — Add a new section at the top with today's date and the changes described.

5. **Build the release APK**:
   ```bash
   ./gradlew assembleRelease
   ```
   If build fails, fix the issue before continuing.

6. **Commit, tag, and push**:
   ```bash
   git add -A
   git commit -m "release: v<version>"
   git tag v<version>
   git push origin main --tags
   ```

7. **Create GitHub Release**:
   ```bash
   gh release create v<version> \
     app/build/outputs/apk/release/app-release.apk \
     --title "v<version>" \
     --notes "<changelog entry for this version>" \
     --repo tronghuy5555/bongdatv
   ```

8. **Confirm** — Report the release URL and what version the app will now auto-update to.

## Build Types
- **Debug** (`./gradlew assembleDebug`): For local testing on emulator/device. Output: `app/build/outputs/apk/debug/app-debug.apk`
- **Release** (`./gradlew assembleRelease`): For production releases. Signed with release keystore, ProGuard minified. Output: `app/build/outputs/apk/release/app-release.apk`

## Notes
- Always use release APK for GitHub Releases (production)
- Use debug APK only for local testing via `deploy-test` skill
- Tag format: `v1.0.0`, `v1.1.0`, etc.
- The app's `UpdateChecker` will pick up the new release automatically on next launch
- Keystore is at `keystore/release.jks` (gitignored, keep safe!)
