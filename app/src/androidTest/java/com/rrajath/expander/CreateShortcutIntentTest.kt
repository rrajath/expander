package com.rrajath.expander

import android.content.ComponentName
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateShortcutIntentTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun processTextIntent_resolvesToThisAppWithCreateShortcutLabel() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pm = context.packageManager

        val intent = Intent(Intent.ACTION_PROCESS_TEXT).setType("text/plain")
        val resolveInfos = pm.queryIntentActivities(intent, 0)

        val match = resolveInfos.any { resolveInfo ->
            resolveInfo.activityInfo.packageName == context.packageName &&
                resolveInfo.loadLabel(pm).toString() == "Create a Shortcut"
        }

        assertTrue(
            "Expected a PROCESS_TEXT handler in ${context.packageName} labeled 'Create a Shortcut'",
            match
        )
    }

    @Test
    fun processTextLaunch_prefillsExpansionOnAddSnippetScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val intent = Intent(Intent.ACTION_PROCESS_TEXT).apply {
            component = ComponentName(context, MainActivity::class.java)
            type = "text/plain"
            putExtra(Intent.EXTRA_PROCESS_TEXT, "my selected text")
        }

        ActivityScenario.launch<MainActivity>(intent).use {
            composeTestRule.onAllNodesWithText("Add Snippet").onFirst().assertIsDisplayed()
            composeTestRule.onNodeWithText("my selected text").assertIsDisplayed()
        }
    }
}
