# Expander Privacy Policy

Last updated: 2026-09-02

Expander is a text expansion tool for Android. It replaces short trigger words
you configure with longer text as you type, across all apps on your device.

## Data collection

Expander does not collect, transmit, sell, or share any personal or sensitive
user data. The app has no internet permission, contains no analytics libraries,
no advertising libraries, and no crash reporting.

## Accessibility service

Expander uses Android's AccessibilityService API. This is required for
system-wide text expansion: it is the only mechanism by which an app can detect
a trigger word in another app's text field and replace it.

When you enable the service:

- Expander can read the text content of editable fields in other apps as you
  type in them, and replace that text.
- This text is processed only in memory, only for the field you are currently
  typing in, and only to find a configured trigger and substitute its expansion.
- Expander does not store, log, or transmit the contents of other apps.
- No text ever leaves your device.

Expander is a productivity tool. It is not a designated accessibility tool for
people with disabilities, so `android:isAccessibilityTool` is set to `false` and
the app shows a prominent in-app disclosure and requires your explicit opt-in
before the accessibility service can be enabled.

You can disable the accessibility service at any time from Android Settings under
Accessibility.

## Display over other apps (optional)

If a build of Expander includes the suggestion overlay feature, the app also
requests the "Display over other apps" (`SYSTEM_ALERT_WINDOW`) permission. This
is optional, is only requested if you turn the suggestion overlay on, and is used
solely to draw the shortcut suggestion list above the keyboard. Plain
space-triggered expansion does not use it.

## Data storage

Your snippets, settings, and theme preference are stored locally on your device.
They are included in Android's system backup if you have that enabled, which is
governed by Google's backup terms, not by Expander.

## Contact

Questions about this policy can be raised on the project's issue tracker.
