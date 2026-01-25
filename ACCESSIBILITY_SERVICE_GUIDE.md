# Accessibility Service Persistence Guide

## The Issue

You may have noticed that the Accessibility Service needs to be re-enabled after app updates. This is **intentional behavior by Android**, not a bug in the app.

## Why This Happens

Starting from **Android 11 (API 30)**, Android automatically disables accessibility services when an app is updated as a **security measure**. This prevents malicious app updates from silently gaining powerful accessibility permissions without user consent.

This is documented in Android's official documentation:
- [Accessibility Service Security](https://developer.android.com/guide/topics/ui/accessibility/service#security)

## What We've Done to Minimize This

While we cannot completely prevent Android from disabling the service, we've implemented several improvements:

### 1. **Consistent App Signing** ✅
The app uses consistent keystore signing across releases. This helps Android recognize the app as legitimate and can reduce (but not eliminate) service disabling.

### 2. **Data Backup & Restore** ✅
We've configured proper backup rules so your snippets and settings are preserved across:
- App updates
- Device transfers
- Cloud backups

Files backed up:
- All snippets (database)
- App preferences
- Service settings

### 3. **Direct Boot Support** ✅
The service is configured with `android:directBootAware="true"`, which helps it survive device reboots when enabled.

### 4. **Automatic Detection** ✅
The app now **automatically detects** when the accessibility service is disabled and shows a prominent warning banner with a one-tap button to open Accessibility Settings.

### 5. **Easy Re-enabling**
When you see the warning banner:
1. Tap the "Enable" button
2. Find "Expander" in the accessibility list
3. Toggle it on
4. Return to the app

The entire process takes ~10 seconds.

## When Re-enabling is Required

You'll need to re-enable the accessibility service after:
- ❌ **App updates** (most common)
- ❌ **System updates** (occasionally)
- ❌ **Device restarts** (rare, but possible)
- ✅ **Killing the app** - No re-enable needed
- ✅ **Switching between apps** - No re-enable needed

## Comparison with Other Apps

**All** text expansion apps on Android face this same limitation. Popular apps like:
- Texpand
- Text Expander
- AutoText
- Any keyboard with text expansion

All require re-enabling accessibility services after updates. This is a platform limitation, not an app-specific issue.

## Why We Can't Work Around This

Some might ask: "Can't you just use a different approach?"

Unfortunately, no:
- ✅ **Accessibility Service** - Most powerful, best user experience, requires re-enable
- ❌ **Keyboard IME** - Requires switching keyboards, limited trigger detection
- ❌ **Clipboard Monitor** - Poor UX, requires manual paste, privacy concerns
- ❌ **Overlay Permissions** - Can't inject text into other apps

The Accessibility Service approach provides the best user experience despite the re-enabling requirement.

## Best Practices for Users

### To Minimize Disruption:

1. **Install signed releases** - Always use releases from our GitHub Releases page (these are properly signed)

2. **Enable automatic re-enabling reminder** - The app will show you a prominent banner when the service is disabled

3. **Check after updates** - After updating the app, look for the warning banner on the home screen

4. **Create a routine** - Make it part of your update routine:
   - Update app → Open app → Tap "Enable" button → Done

### For Developers/Power Users:

If you're building the app locally:
1. **Use consistent signing** - Always sign with the same keystore for debug and release builds
2. **Don't switch between signed and unsigned** - This will always trigger service disabling
3. **Use the release workflow** - Our GitHub Actions workflow ensures consistent signing

## Technical Details

### How Android Disables Services

When an app is updated, Android's Package Manager:
1. Detects the APK change
2. Checks if the app has accessibility services
3. Automatically disables all accessibility services from that app
4. Requires explicit user re-enablement

This is hardcoded in AOSP (Android Open Source Project) and cannot be bypassed.

### What Gets Preserved

✅ Your snippets (database is backed up)
✅ App settings and preferences
✅ Theme preference
✅ Service enabled preference (internal flag)
❌ System-level accessibility service enablement

## Future Improvements

We're exploring:
- [ ] Post-update notification reminding users to re-enable
- [ ] Quick Settings tile for faster access to settings
- [ ] Setup wizard on first launch after update

## Questions?

**Q: Is this a bug?**
A: No, it's intentional Android security behavior.

**Q: Can you fix it?**
A: No app can bypass this Android platform restriction.

**Q: Why do some apps not require this?**
A: If they don't require re-enabling, they're either:
- Not using accessibility services
- You haven't updated them (check recent updates)
- Using a less effective approach for text expansion

**Q: Is my data safe?**
A: Yes! Your snippets and settings are fully backed up and restored.

**Q: How often will this happen?**
A: Only when you update the app. If you update monthly, you'll need to re-enable monthly.

## Reporting Issues

If you experience accessibility service issues **not related to app updates**:
1. Check the warning banner in the app
2. Review your device's accessibility settings
3. Ensure the app has all required permissions
4. Report the issue on GitHub with:
   - Android version
   - Device model
   - Steps to reproduce

For update-related re-enabling, this is expected behavior and not a bug.
