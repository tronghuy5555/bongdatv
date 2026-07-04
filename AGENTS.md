# BongDaTV Agent Instructions

Read `CLAUDE.md` for project overview, stack, architecture, build commands, API endpoints, UI language, and D-pad navigation conventions.

## Mandatory Project Workflows

This repo keeps Codex workflow skills in `.codex/skills`. Treat these as mandatory project-local workflow documents, even if they are not listed in the global Codex skill inventory.

Before starting any task that matches one of the triggers below:

1. Read the matching `.codex/skills/<skill-name>/SKILL.md` completely.
2. Follow its steps in order.
3. Do not skip requested validation/build/release steps unless the user explicitly changes the workflow.
4. Report any blocked step clearly instead of silently substituting a different process.

Use these mappings:

- Version-only changes: read `.codex/skills/bump-version/SKILL.md` when the user asks to bump version, increment version, prepare next version, or update `versionCode`/`versionName` only.
- Changelog changes: read `.codex/skills/changelog/SKILL.md` when the user asks to update changelog, add to changelog, log changes, or record release notes.
- Emulator/device testing: read `.codex/skills/deploy-test/SKILL.md` when the user asks to deploy, install, run on TV, try it, test on device, or verify behavior on the emulator.
- Production release: read `.codex/skills/publish/SKILL.md` when the user asks to publish, release, ship, create a new version, or create a production GitHub Release. Follow its nested requirement to read and run the `bump-version` and `changelog` workflows before release build/tag/publish steps.

## Repo Rules

- Keep all user-facing app text in Vietnamese.
- Use Android TV/D-pad interaction patterns: focusable elements, visible focus state, and no touch-only behavior.
- For player/video changes, verify with a debug build and emulator/device screenshot when possible.
- Do not commit, tag, push, or publish unless the user explicitly asks.
