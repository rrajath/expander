package com.rrajath.expander

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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

            // enableEdgeToEdge() only sets the status/nav bar icon style once, based on the
            // system theme at launch. It doesn't react to the in-app Light/Dark/System choice,
            // so forcing Light theme while the system is in Dark mode left white (unreadable)
            // status bar icons over the light background. Keep it in sync with darkTheme instead.
            val view = LocalView.current
            SideEffect {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
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