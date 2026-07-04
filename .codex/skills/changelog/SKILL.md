---
name: changelog
description: Add an entry to BongDaTV CHANGELOG.md. Use when the user says "update changelog", "add to changelog", "log changes", or asks to record release notes.
---

# Update Changelog

Add a new entry to the project changelog.

## Steps

1. Get change details. Ask what was added, fixed, or changed if the user did not provide enough detail.

2. Read the current `CHANGELOG.md` to preserve the existing format.

3. Read the current `versionName` from `app/build.gradle.kts`.

4. Add a new entry at the top of the changelog below the `# Changelog` header:

   ```markdown
   ## [<version>] - <YYYY-MM-DD>
   ### Added
   - New features

   ### Fixed
   - Bug fixes

   ### Changed
   - Changes to existing features
   ```

   Use today's date. Include only sections that have entries.

5. Report what was added. Do not commit unless asked.
