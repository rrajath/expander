# Release and Versioning Guide

This project uses automatic semantic versioning (semver) and GitHub releases for every push to the main branch.

## How It Works

### Automatic Releases

Every push to `main` or `master` branch automatically:
1. Calculates the next version number using semver
2. Updates `versionCode` and `versionName` in `app/build.gradle.kts`
3. Builds debug and release APKs
4. Creates a Git tag (e.g., `v1.2.3`)
5. Creates a GitHub release with:
   - Commit history since last release
   - Debug APK attached
   - Release APK attached (signed if keystore is configured)

### Semantic Versioning

The version format is: `MAJOR.MINOR.PATCH` (e.g., `1.2.3`)

**Version increments are determined by your commit message:**

| Commit Message Contains | Version Bump | Example |
|------------------------|--------------|---------|
| `[major]` or `breaking change` | Major version | `1.0.0` → `2.0.0` |
| `[minor]` or `feat:` | Minor version | `1.0.0` → `1.1.0` |
| Anything else | Patch version | `1.0.0` → `1.0.1` |

### Examples

```bash
# Patch version bump (1.0.0 → 1.0.1)
git commit -m "fix: Fixed trigger case sensitivity bug"

# Minor version bump (1.0.0 → 1.1.0)
git commit -m "feat: Added dark mode support"
git commit -m "[minor] Added new feature"

# Major version bump (1.0.0 → 2.0.0)
git commit -m "[major] Complete UI redesign"
git commit -m "breaking change: Changed database schema"
```

## Version Code Calculation

`versionCode` is calculated as: `MAJOR * 10000 + MINOR * 100 + PATCH`

Examples:
- `v1.0.0` → versionCode `10000`
- `v1.2.3` → versionCode `10203`
- `v2.5.7` → versionCode `20507`

This ensures version codes always increment and supports up to:
- 999 major versions
- 99 minor versions per major
- 99 patches per minor

## Workflows

### 1. Release Workflow (`.github/workflows/release.yml`)

**Triggers:** Push to `main` or `master`

**Actions:**
- Generates semantic version
- Updates app version
- Builds signed APKs
- Creates GitHub release
- Attaches APKs to release

### 2. Build Workflow (`.github/workflows/build-apks.yml`)

**Triggers:** Pull requests and pushes to `develop`/`dev` branches

**Actions:**
- Builds debug and release APKs
- Uploads as artifacts (no release created)

## Making a Release

### Standard Release (Patch)

```bash
git add .
git commit -m "Fix: Resolved text expansion bug"
git push origin main
```

This creates version `v0.0.1` (or increments patch: `v1.2.3` → `v1.2.4`)

### Feature Release (Minor)

```bash
git add .
git commit -m "feat: Added export to CSV feature"
git push origin main
```

This increments minor version: `v1.2.3` → `v1.3.0`

### Breaking Change Release (Major)

```bash
git add .
git commit -m "[major] Redesigned database schema"
git push origin main
```

This increments major version: `v1.2.3` → `v2.0.0`

## Downloading Releases

### From GitHub Releases

1. Go to your repository on GitHub
2. Click on "Releases" (right sidebar)
3. Select the version you want
4. Download the APK from the "Assets" section:
   - `expander-X.Y.Z-debug.apk` - Debug build
   - `expander-X.Y.Z-release.apk` - Release build (signed)

### From GitHub Actions Artifacts

1. Go to "Actions" tab
2. Click on the workflow run
3. Scroll to "Artifacts" section
4. Download the APK

Artifacts are kept for 90 days.

## First Release

If this is your first release, the workflow will start at `v0.0.1`. To start with a different version:

```bash
# Create initial tag
git tag -a v1.0.0 -m "Initial release"
git push origin v1.0.0

# Next push will increment from v1.0.0
```

## Troubleshooting

**Release APK is unsigned:**
- Ensure you've set up the keystore secrets in GitHub (see `SIGNING_SETUP.md`)
- Required secrets: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`

**Version not incrementing correctly:**
- Check your commit message format
- Use `[major]`, `[minor]`, or `feat:` to control version bumps
- Default is patch version increment

**Git tag already exists:**
- This shouldn't happen with auto-incrementing
- If needed, delete the tag locally and remotely:
  ```bash
  git tag -d v1.2.3
  git push origin :refs/tags/v1.2.3
  ```

## Manual Version Control

If you need to manually set a version:

1. Create and push the tag:
   ```bash
   git tag -a v2.5.0 -m "Release v2.5.0"
   git push origin v2.5.0
   ```

2. The next automatic release will increment from this version

## Best Practices

1. **Use conventional commits** for clear version history
2. **Test in develop branch** before merging to main
3. **Use [major] sparingly** - breaking changes should be rare
4. **Keep releases small** - frequent small releases are better than large ones
5. **Review release notes** after each release to ensure accuracy
