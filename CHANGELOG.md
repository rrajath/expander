# Changelog

All notable changes to Expander are documented in this file, newest first.
Entries under `## Unreleased` are rolled into a dated `## [x.y.z]` section
when the matching `vx.y.z` tag is pushed. The `[1.0.4]`-`[1.1.0]` sections
were backfilled by hand; sections older than that are dated by commit date.

## Unreleased

- Added a data-use disclosure for the accessibility service. The first time you
  tap **Enable** on the home banner or **Accessibility Settings** in Settings,
  Expander now shows a dialog explaining what the service can read, why it needs
  it, and that nothing leaves your device, and asks you to agree before it opens
  the system Accessibility Settings screen. The same text is available any time
  under **Settings → Privacy and data use**. This is required by Google Play
  policy for an app that uses the accessibility API for a non-accessibility-tool
  purpose.
- Marked the service `android:isAccessibilityTool="false"` and dropped the
  unused `flagReportViewIds` and `flagRetrieveInteractiveWindows` accessibility
  flags, so the service only requests what plain text expansion needs. Rewrote
  the service description shown in system settings.
- Added `docs/PRIVACY.md` and a Play Protect troubleshooting section to the
  README.

## [1.1.1] - 2026-08-27

- Changed the release pipeline to be tag-triggered. Pushing a commit to
  `main` no longer creates a GitHub release; instead, pushing a `vX.Y.Z`
  tag builds a debug APK, a signed release APK and a signed release AAB,
  rolls this "Unreleased" section into a dated `[X.Y.Z]` section, and
  publishes a GitHub Release with the three artifacts attached. The version
  name now lives in `gradle.properties` (`VERSION_NAME`) and CI overrides
  it from the tag; `versionCode` is derived from it.
- Fixed backspace-undo not working after expanding a snippet with dynamic
  placeholders. Undo detection assumed the keyboard removed exactly one
  character, which held for short static expansions but not for dynamic
  values containing punctuation and digits (e.g. `2026-08-26`), where the
  keyboard often deletes a whole chunk on a single backspace. The first
  edit that shortens the freshly-inserted expansion now reverts it to the
  trigger regardless of how many characters were removed. The service also
  ignores its own `ACTION_SET_TEXT` echo event and drops the undo history
  once the user types past the expansion.

## [1.1.0] - 2026-08-02

- Added date math to dynamic placeholders: any date-based placeholder
  (`date`, `datetime`, `day`, `day_long`, `month`, `month_long`, `year`,
  `year_short`, `week_num`) can now be offset by appending a signed amount
  and a unit letter (`d`/`w`/`m`/`y`) right after its name, e.g.
  `{{date+3d}}`, `{{date-2w}}`, `{{date+1y:dd/MM/yyyy}}`.

## [1.0.5] - 2026-07-31

- Made the smart punctuation character set configurable in Settings via a
  space-separated text field, defaulting to `? ! , . ; : ) " ' ] } \` ~`.
- Fixed status bar icons being unreadable (white-on-light) when the in-app
  theme was manually set to Light while the system was in Dark mode.
  `enableEdgeToEdge()` only set icon appearance once at launch based on the
  system theme; the app now keeps status/navigation bar icon appearance in
  sync with the resolved app theme reactively.
- Added "Smart Punctuation Spacing": if a punctuation character is typed
  after a stray trailing space (e.g. via a keyboard's long-press symbol
  popup, producing "you ? "), the space is moved from before the
  punctuation to after it ("you? "). Toggleable in Settings, on by default.

## [1.0.4] - 2026-07-11

- Added a debug build type with a distinct application ID (`.debug`
  suffix), version name suffix, and "Expander Debug" label so debug builds
  can be installed side-by-side with release builds.
- Added a "Create a Shortcut" option to the system text-selection menu.
  Selecting text in any app now shows this action via an
  `ACTION_PROCESS_TEXT` activity-alias; tapping it opens the Add Snippet
  screen with the selection prefilled as the expansion. Includes JVM unit
  tests and instrumented UI/intent tests, and bumps `espresso-core` to
  3.7.0 and `test-ext-junit` to 1.3.0 to fix an Espresso/Android image
  incompatibility.

## 2026-05-15

- Updated the app icons with a new design.

## 2026-05-11

- Added `year_short` and `week_num` placeholder examples to the in-app UI
  and README.

## 2026-05-10

- Added `year_short` and `week_num` dynamic snippet placeholders.

## 2026-01-25

- Enhanced the accessibility service experience: added backspace-undo
  tracking to `TextExpansionService`, improved empty/guidance states in
  the snippet list screen, added `ACCESSIBILITY_SERVICE_GUIDE.md`, and
  tightened backup/data-extraction rules.
- Added a GitHub Actions release workflow, plus `RELEASE_GUIDE.md` and
  expanded `SIGNING_SETUP.md` documentation.
- Fixed release build errors.

## 2026-01-24

- Configured GitHub Actions for CI builds, along with APK signing setup
  (`SIGNING_SETUP.md`, `keystore.properties.template`).
- Made snippet triggers case-insensitive.
- Initial commit: `.gitignore` and `LICENSE`.

## 2026-01-22

- Added Settings screen, theme preferences (Light/Dark/System), and
  related UI polish.
- Initial implementation: project scaffold, Room database, snippet
  CRUD, and the core accessibility-service-based text expansion engine.
