# APK Signing Setup Guide

This guide explains how to set up APK signing for both local development and GitHub Actions CI/CD.

## 1. Create a Keystore (First Time Only)

Run this command to create a new keystore:

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias expander-key
```

You'll be prompted for:
- **Keystore password**: Choose a strong password (remember this!)
- **Key password**: Can be the same or different from keystore password
- **Name, Organization, etc.**: Fill in your details

**Important**: Keep your keystore file and passwords safe! Store backups securely. You cannot update your app in the Play Store without the same keystore.

## 2. Local Development Setup

1. Copy the template file:
   ```bash
   cp keystore.properties.template keystore.properties
   ```

2. Edit `keystore.properties` with your actual values:
   ```properties
   storeFile=/absolute/path/to/your/release-keystore.jks
   storePassword=your_store_password
   keyAlias=expander-key
   keyPassword=your_key_password
   ```

3. Build signed release APK locally:
   ```bash
   ./gradlew assembleRelease
   ```

   The signed APK will be at: `app/build/outputs/apk/release/app-release.apk`

## 3. GitHub Actions Setup

To enable automatic signed builds in GitHub Actions:

### Step 1: Encode your keystore to base64

```bash
base64 -i release-keystore.jks | pbcopy
```

This copies the base64-encoded keystore to your clipboard.

### Step 2: Add GitHub Secrets

Go to your GitHub repository:
1. Settings → Secrets and variables → Actions
2. Click "New repository secret" and add each of the following:

| Secret Name | Value |
|-------------|-------|
| `KEYSTORE_BASE64` | Paste the base64 string from Step 1 |
| `KEYSTORE_PASSWORD` | Your keystore password |
| `KEY_ALIAS` | Your key alias (e.g., `expander-key`) |
| `KEY_PASSWORD` | Your key password |

### Step 3: Test the workflow

Push code to your main branch:
```bash
git add .
git commit -m "Set up APK signing"
git push origin main
```

Go to the "Actions" tab on GitHub to see the release workflow running. Once complete:
- A new GitHub release will be created with the signed APKs attached
- You can download the APKs from the "Releases" section

## Security Notes

- ✅ `keystore.properties` is in `.gitignore` - never commit it
- ✅ `*.jks` files are in `.gitignore` - never commit your keystore
- ✅ GitHub Secrets are encrypted and not visible in logs
- ⚠️ Keep backups of your keystore in a secure location
- ⚠️ Never share your keystore or passwords publicly

## Important: Accessibility Service Persistence

**Why consistent signing matters:**

Android disables accessibility services when it detects an app update from a different source or with a different signature. To minimize disruptions:

1. **Always use the same keystore** for all releases
2. **Never switch between signed and unsigned** APKs
3. **Use GitHub Actions for releases** - ensures consistent signing
4. **Don't mix debug and release** builds on the same device

With consistent signing:
- ✅ Snippets and settings are preserved
- ✅ App recognizes previous installation
- ⚠️ Accessibility service still requires re-enabling (Android security policy)

See `ACCESSIBILITY_SERVICE_GUIDE.md` for details on why re-enabling is required after updates.

## Troubleshooting

**"Keystore not found" error locally:**
- Ensure the `storeFile` path in `keystore.properties` is absolute
- Verify the keystore file exists at that location

**GitHub Actions signing fails:**
- Verify all 4 secrets are set correctly in GitHub
- Check that the base64 encoding was done correctly
- Review the Actions logs for specific error messages

**"Wrong password" errors:**
- Double-check your passwords in `keystore.properties` or GitHub Secrets
- Ensure there are no extra spaces in the values
