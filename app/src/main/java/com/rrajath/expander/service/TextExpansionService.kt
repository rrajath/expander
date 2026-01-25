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
                processTextForExpansion(source, currentText)
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
