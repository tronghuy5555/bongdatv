---
name: publish
description: Build and publish a BongDaTV production release by first running the project-local bump-version and changelog workflows, then creating the release APK, git tag, and GitHub Release. Use when the user says "publish", "release", "ship", "new version", or asks to create a production release.
---

# Publish Release

Automate the full release process for BongDaTV.

## Steps

1. Ask what changed. Use the user's existing description if already provided.

2. Run the version workflow:
   - Read `.codex/skills/bump-version/SKILL.md` completely.
   - Follow its steps to determine the bump type and update `app/build.gradle.kts`.
   - If the user already bumped the version for this release in the current worktree, treat the workflow as satisfied after confirming `versionCode` and `versionName`; do not bump twice unless the user explicitly asks.

3. Run the changelog workflow:
   - Read `.codex/skills/changelog/SKILL.md` completely.
   - Follow its steps using the release version from `app/build.gradle.kts`.
   - If `CHANGELOG.md` already has an entry for the release version, update that entry instead of adding a duplicate.

4. Build the release APK:

   ```bash
   ./gradlew assembleRelease
   ```

   If the build fails, fix the issue before continuing.

5. Commit, tag, and push:

   ```bash
   git add -A
   git commit -m "release: v<version>"
   git tag v<version>
   git push origin main --tags
   ```

6. Create the GitHub Release:

   ```bash
   gh release create v<version> \
     app/build/outputs/apk/release/app-release.apk \
     --title "v<version>" \
     --notes "<changelog entry for this version>" \
     --repo tronghuy5555/bongdatv
   ```

7. Confirm the release URL and the version the app will auto-update to.

## Build Types

- Debug: use `./gradlew assembleDebug` for local testing on emulator or device. Output: `app/build/outputs/apk/debug/app-debug.apk`.
- Release: use `./gradlew assembleRelease` for production releases. Signed with release keystore and minified by ProGuard. Output: `app/build/outputs/apk/release/app-release.apk`.

## Notes

- Always use release APK for GitHub Releases.
- Use debug APK only for local testing via the `deploy-test` skill.
- Treat this skill as the release orchestrator. Do not duplicate or bypass the `bump-version` and `changelog` workflows.
- Tag format is `v1.0.0`, `v1.1.0`, etc.
- The app's `UpdateChecker` will pick up the new release automatically on next launch.
- The release keystore is at `keystore/release.jks` and is gitignored.
