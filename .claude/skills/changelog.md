---
name: changelog
description: Add an entry to CHANGELOG.md. Use when the user says "update changelog", "add to changelog", or "log changes".
---

# Update Changelog

Add a new entry to the project changelog.

## Steps

1. **Get change details** — Ask what was added/fixed/changed if not already provided.

2. **Read current `CHANGELOG.md`** to get the latest version and format.

3. **Read current `versionName`** from `app/build.gradle.kts`.

4. **Add new entry** at the top of the changelog (below the `# Changelog` header):
   ```markdown
   ## [<version>] - <YYYY-MM-DD>
   ### Added
   - New features

   ### Fixed
   - Bug fixes

   ### Changed
   - Changes to existing features
   ```
   Only include sections that have entries. Use today's date.

5. **Report** what was added. Do not commit unless asked.
