package com.rrajath.expander.util

import android.content.Context

/**
 * Tracks whether the user has seen and accepted the in-app disclosure that
 * explains what the accessibility service can read and what Expander does with
 * it.
 *
 * Google Play policy requires a prominent, standalone in-app disclosure and an
 * affirmative opt-in before an app may use the AccessibilityService API for a
 * non-accessibility-tool purpose. The disclosure text lives in
 * `R.string.accessibility_disclosure_*`; this object only records the decision.
 *
 * Stored in the same `expander_prefs` file the service uses for its own flags so
 * every service-gating preference sits in one place.
 */
object AccessibilityDisclosure {

    private const val PREFS_NAME = "expander_prefs"
    private const val KEY_ACCEPTED = "accessibility_disclosure_accepted"

    /** True once the user has tapped "Agree and continue" on the disclosure. */
    fun isAccepted(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ACCEPTED, false)

    fun setAccepted(context: Context, accepted: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ACCEPTED, accepted)
            .apply()
    }
}
