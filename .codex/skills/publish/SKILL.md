---
name: publish
description: Build a BongDaTV release APK, bump version, update changelog, create a git tag, and publish a GitHub Release with the APK attached. Use when the user says "publish", "release", "ship", "new version", or asks to create a production release.
---

# Publish Release

Automate the full release process for BongDaTV.

## Steps

1. Ask what changed. Use the user's existing description if already provided.

2. Determine the version bump. Ask whether this is patch, minor, or major. Default to minor only if the user leaves it unclear.

3. Bump the version in `app/build.gradle.kts`:
   - Increment `versionCode` by 1
   - Update `versionName` to the new semver

4. Update `CHANGELOG.md`. Add a new section at the top with today's date and the described changes.

5. Build the release APK:

   ```bash
   ./gradlew assembleRelease
   ```

   If the build fails, fix the issue before continuing.

6. Commit, tag, and push:

   ```bash
   git add -A
   git commit -m "release: v<version>"
   git tag v<version>
   git push origin main --tags
   ```

7. Create the GitHub Release:

   ```bash
   gh release create v<version> \
     app/build/outputs/apk/release/app-release.apk \
     --title "v<version>" \
     --notes "<changelog entry for this version>" \
     --repo tronghuy5555/bongdatv
   ```

8. Confirm the release URL and the version the app will auto-update to.

## Build Types

- Debug: use `./gradlew assembleDebug` for local testing on emulator or device. Output: `app/build/outputs/apk/debug/app-debug.apk`.
- Release: use `./gradlew assembleRelease` for production releases. Signed with release keystore and minified by ProGuard. Output: `app/build/outputs/apk/release/app-release.apk`.

## Notes

- Always use release APK for GitHub Releases.
- Use debug APK only for local testing via the `deploy-test` skill.
- Tag format is `v1.0.0`, `v1.1.0`, etc.
- The app's `UpdateChecker` will pick up the new release automatically on next launch.
- The release keystore is at `keystore/release.jks` and is gitignored.
