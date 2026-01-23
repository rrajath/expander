package com.rrajath.expander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.rrajath.expander.ui.navigation.NavGraph
import com.rrajath.expander.ui.theme.ExpanderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpanderTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}