package com.rrajath.expander.util

/**
 * Pure helper for extracting selected text from an ACTION_PROCESS_TEXT launch.
 * Kept free of Android framework types so it can be unit tested on the JVM.
 */
object ProcessTextHelper {

    const val ACTION_PROCESS_TEXT = "android.intent.action.PROCESS_TEXT"

    /**
     * Returns the selected text as a String only when [action] is
     * [ACTION_PROCESS_TEXT] and [processText] is non-null and not blank.
     * The text is returned verbatim (never trimmed).
     */
    fun extractSelectedText(action: String?, processText: CharSequence?): String? {
        if (action != ACTION_PROCESS_TEXT) return null
        if (processText == null || processText.isBlank()) return null
        return processText.toString()
    }
}
