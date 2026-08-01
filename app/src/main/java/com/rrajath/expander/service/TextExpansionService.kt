package com.rrajath.expander.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.os.bundleOf
import com.rrajath.expander.data.AppDatabase
import com.rrajath.expander.data.Snippet
import com.rrajath.expander.data.SnippetRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class TextExpansionService : AccessibilityService() {

    private lateinit var repository: SnippetRepository
    private lateinit var prefs: SharedPreferences
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var snippetsCache: List<Snippet> = emptyList()
    private var lastProcessedText = ""

    // Undo tracking
    private var lastExpansion: ExpansionHistory? = null

    private data class ExpansionHistory(
        val trigger: String,
        val expansion: String,
        val textBeforeTrigger: String
    )

    companion object {
        private const val PREFS_NAME = "expander_prefs"
        private const val KEY_SERVICE_ENABLED = "service_enabled"
        private const val KEY_SMART_PUNCTUATION_ENABLED = "smart_punctuation_enabled"
        private const val KEY_SMART_PUNCTUATION_CHARS = "smart_punctuation_chars"

        // Punctuation that should never have a space before it. If the keyboard's
        // symbol popup inserts one of these after a trailing space, the space is
        // removed and moved to after the punctuation instead. User-configurable via
        // Settings as a space-separated string; this is only the default.
        const val DEFAULT_SMART_PUNCTUATION_CHARS = "? ! , . ; : ) \" ' ] } ` ~"

        fun isServiceEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_SERVICE_ENABLED, true)
        }

        fun setServiceEnabled(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_SERVICE_ENABLED, enabled)
                .apply()
        }

        fun isSmartPunctuationEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_SMART_PUNCTUATION_ENABLED, true)
        }

        fun setSmartPunctuationEnabled(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_SMART_PUNCTUATION_ENABLED, enabled)
                .apply()
        }

        /** Raw, space-separated string as edited by the user (may contain in-progress typing). */
        fun getSmartPunctuationCharsRaw(context: Context): String {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_SMART_PUNCTUATION_CHARS, DEFAULT_SMART_PUNCTUATION_CHARS)
                ?: DEFAULT_SMART_PUNCTUATION_CHARS
        }

        fun setSmartPunctuationCharsRaw(context: Context, raw: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SMART_PUNCTUATION_CHARS, raw)
                .apply()
        }

        /** Parses the raw space-separated string into single-character tokens, ignoring the rest. */
        fun parseSmartPunctuationChars(raw: String): Set<Char> {
            return raw.split(" ", "\t", "\n")
                .filter { it.length == 1 }
                .map { it[0] }
                .toSet()
        }

        /**
         * Checks if the accessibility service is actually enabled in system settings.
         * This is different from isServiceEnabled which only checks our internal preference.
         */
        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expectedComponentName = "${context.packageName}/${TextExpansionService::class.java.name}"
            val enabledServices = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return enabledServices?.contains(expectedComponentName) == true
        }
    }

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(applicationContext)
        repository = SnippetRepository(database.snippetDao())
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load snippets into cache
        serviceScope.launch {
            repository.getEnabledSnippets().collect { snippets ->
                snippetsCache = snippets
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!isServiceEnabled(this)) return

        // Only process text change events
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return

        val source = event.source ?: return

        try {
            val currentText = source.text?.toString() ?: ""

            // Check for backspace undo
            if (shouldUndoExpansion(currentText)) {
                undoExpansion(source)
                lastProcessedText = currentText
                source.recycle()
                return
            }

            // Check if text ends with a space (trigger for expansion)
            if (currentText.endsWith(" ") && currentText.isNotEmpty()) {
                if (!applySmartPunctuationSpacing(source, currentText)) {
                    processTextForExpansion(source, currentText)
                }
            }

            lastProcessedText = currentText
        } catch (e: Exception) {
            // Silently handle errors to avoid service crashes
        } finally {
            source.recycle()
        }
    }

    private fun shouldUndoExpansion(currentText: String): Boolean {
        val history = lastExpansion ?: return false

        // Check if user deleted one character from the expanded text
        val expectedTextAfterBackspace = history.textBeforeTrigger + history.expansion.dropLast(1)
        return currentText == expectedTextAfterBackspace
    }

    private fun undoExpansion(source: AccessibilityNodeInfo) {
        val history = lastExpansion ?: return

        try {
            // Restore the original trigger text
            val restoredText = history.textBeforeTrigger + history.trigger

            val arguments = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, restoredText)
            }

            source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

            // Move cursor to the end
            arguments.clear()
            arguments.putInt(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT,
                restoredText.length
            )
            arguments.putInt(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT,
                restoredText.length
            )
            source.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments)

            // Clear the undo history after using it
            lastExpansion = null
        } catch (e: Exception) {
            // Silently handle errors
        }
    }

    /**
     * Fixes text like "how are you ? " (space typed before punctuation, e.g. via the
     * keyboard's long-press symbol popup) into "how are you? " by moving the space(s)
     * from before the punctuation to after it. Returns true if a fix was applied.
     */
    private fun applySmartPunctuationSpacing(source: AccessibilityNodeInfo, text: String): Boolean {
        if (!isSmartPunctuationEnabled(this)) return false

        val beforeTrailingSpace = text.dropLast(1)
        val punct = beforeTrailingSpace.lastOrNull() ?: return false
        val punctuationChars = parseSmartPunctuationChars(getSmartPunctuationCharsRaw(this))
        if (punct !in punctuationChars) return false

        val punctIndex = beforeTrailingSpace.length - 1
        var spaceStart = punctIndex
        while (spaceStart > 0 && beforeTrailingSpace[spaceStart - 1] == ' ') spaceStart--
        val spacesBeforePunct = punctIndex - spaceStart
        if (spacesBeforePunct == 0) return false

        val trigger = " ".repeat(spacesBeforePunct) + punct
        val expansion = "$punct "

        expandText(source, text, trigger, expansion)
        return true
    }

    private fun processTextForExpansion(source: AccessibilityNodeInfo, text: String) {
        // Extract the last word (before the space)
        val words = text.trim().split(Regex("\\s+"))
        if (words.isEmpty()) return

        val lastWord = words.last()

        // Check if this word matches any trigger
        val matchingSnippet = snippetsCache.firstOrNull { snippet ->
            snippet.trigger.equals(lastWord, ignoreCase = true)
        } ?: return

        // Process dynamic placeholders
        val processedExpansion = SnippetProcessor.process(matchingSnippet.expansion)

        // Perform the expansion
        expandText(source, text, lastWord, processedExpansion)
    }

    private fun expandText(
        source: AccessibilityNodeInfo,
        currentText: String,
        trigger: String,
        expansion: String
    ) {
        try {
            // Remove the trigger word and the trailing space
            val textBeforeTrigger = currentText.dropLast(trigger.length + 1)
            val newText = textBeforeTrigger + expansion

            // Save expansion history for undo
            lastExpansion = ExpansionHistory(
                trigger = trigger,
                expansion = expansion,
                textBeforeTrigger = textBeforeTrigger
            )

            // Set the new text using ACTION_SET_TEXT
            val arguments = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newText)
            }

            source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

            // Move cursor to the end
            arguments.clear()
            arguments.putInt(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT,
                newText.length
            )
            arguments.putInt(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT,
                newText.length
            )
            source.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments)

        } catch (e: Exception) {
            // Silently handle errors
        }
    }

    override fun onInterrupt() {
        // Called when the service is interrupted
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
