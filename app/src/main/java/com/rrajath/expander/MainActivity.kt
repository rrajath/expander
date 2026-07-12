package com.rrajath.expander

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.rrajath.expander.ui.navigation.NavGraph
import com.rrajath.expander.ui.theme.ExpanderTheme
import com.rrajath.expander.util.ProcessTextHelper
import com.rrajath.expander.util.ThemeMode
import com.rrajath.expander.util.ThemePreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemePreferences.init(this)
        enableEdgeToEdge()

        val initialExpansion = ProcessTextHelper.extractSelectedText(
            intent?.action,
            intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        )

        setContent {
            val themeMode by ThemePreferences.themeMode.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemInDarkTheme
            }

            ExpanderTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    initialExpansion = initialExpansion
                )
            }
        }
    }
}