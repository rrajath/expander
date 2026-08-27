# Release and Versioning Guide

Releases are cut by pushing a version tag. Pushing commits to `main` does
**not** create a release.

## How It Works

### Cutting a release

Push a tag shaped like `vMAJOR.MINOR.PATCH`:

```bash
git tag v1.2.3
git push origin v1.2.3
```

The `Release` workflow (`.github/workflows/release.yml`) then:

1. Reads the version (`1.2.3`) from the tag name.
2. Builds, with `-PVERSION_NAME=1.2.3` passed to Gradle:
   - debug APK (`assembleDebug`)
   - release APK (`assembleRelease`, signed if keystore secrets are set)
   - release AAB (`bundleRelease`, signed if keystore secrets are set)
3. Rolls the `## Unreleased` section of `CHANGELOG.md` into a dated
   `## [1.2.3] - YYYY-MM-DD` section and commits that back to `main`.
4. Publishes a GitHub Release named `v1.2.3` with the changelog section as
   the body and these three files attached:
   - `expander-1.2.3-debug.apk`
   - `expander-1.2.3-release.apk`
   - `expander-1.2.3-release.aab`

The same three files are also uploaded as a workflow-run artifact
(`expander-1.2.3`, kept 90 days) as a fallback.

### Versioning

`versionName` lives in `gradle.properties` as `VERSION_NAME`. In CI it is
overridden from the tag; for local or manual builds it is whatever the file
says.

`versionCode` is derived from `versionName` in `app/build.gradle.kts`:

```
versionCode = MAJOR * 10000 + MINOR * 100 + PATCH
```

| Tag      | versionName | versionCode |
|----------|-------------|-------------|
| `v1.0.0` | `1.0.0`     | `10000`     |
| `v1.2.3` | `1.2.3`     | `10203`     |
| `v2.5.7` | `2.5.7`     | `20507`     |

This supports up to 99 minor and 99 patch releases per step and always
increments. There is no automatic semver bump from commit messages; you
choose the version when you tag.

### Release notes

The release body is the content that was under `## Unreleased` in
`CHANGELOG.md` at release time. Keep that section current as you work (see
the changelog rule in `CLAUDE.md`) and the release notes take care of
themselves. After the workflow runs, `main` has a new commit
(`chore: roll CHANGELOG Unreleased into v1.2.3`) - pull it before your next
local work.

If `## Unreleased` is empty when you tag, the changelog step fails on
purpose rather than publishing an empty release. Add at least one entry
first.

## Other workflow

`.github/workflows/build-apks.yml` builds debug and release APKs on pull
requests to `main`/`master` and on pushes to `develop`/`dev`, uploading
them as artifacts. It never creates a release.

## Making a release, step by step

1. Make sure `## Unreleased` in `CHANGELOG.md` describes what is shipping.
2. Decide the version number (`MAJOR.MINOR.PATCH`).
3. Tag and push:
   ```bash
   git tag v1.2.3
   git push origin v1.2.3
   ```
4. Watch the run in the repo's **Actions** tab.
5. When it finishes, `git pull` on `main` to pick up the changelog commit.

## Downloading a release

### From GitHub Releases

1. Open the repository on GitHub, click **Releases**.
2. Pick the version, download from **Assets**:
   - `expander-X.Y.Z-debug.apk` - debug build
   - `expander-X.Y.Z-release.apk` - release build (signed)
   - `expander-X.Y.Z-release.aab` - app bundle for Play Store upload

### From a workflow run

1. **Actions** tab, open the `Release` run for the tag.
2. Download the `expander-X.Y.Z` artifact (kept 90 days).

## Troubleshooting

**Release APK / AAB is unsigned:**
- Set the keystore secrets in GitHub (see `SIGNING_SETUP.md`):
  `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
- Without `KEYSTORE_BASE64` the workflow logs a warning and produces
  `expander-X.Y.Z-release-unsigned.apk`.

**Changelog step failed with "No entries under '## Unreleased'":**
- The `## Unreleased` section was empty. Add an entry, then delete and
  re-push the tag (see below).

**Tag already exists / need to redo a release:**
```bash
git tag -d v1.2.3
git push origin :refs/tags/v1.2.3
# fix things, then tag and push again
```
Delete the GitHub Release too if one was already created.

**Wrong version in the build:**
- The version comes only from the tag name. `v1.2.3` -> `1.2.3`. Make sure
  the tag matches `v*.*.*`.

## Manually building a specific version locally

```bash
./gradlew assembleRelease bundleRelease -PVERSION_NAME=1.2.3
```

Or edit `VERSION_NAME` in `gradle.properties` and build normally.
