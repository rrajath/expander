package com.rrajath.expander.ui.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rrajath.expander.ui.SnippetViewModel
import com.rrajath.expander.ui.screens.AddEditSnippetScreen
import com.rrajath.expander.ui.screens.SettingsScreen
import com.rrajath.expander.ui.screens.SnippetListScreen
import com.rrajath.expander.util.ImportExportManager
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

sealed class Screen(val route: String) {
    object SnippetList : Screen("snippet_list")
    object AddSnippet : Screen("add_snippet")
    object EditSnippet : Screen("edit_snippet/{snippetId}") {
        fun createRoute(snippetId: Long) = "edit_snippet/$snippetId"
    }
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: SnippetViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val snippets by viewModel.snippets.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Export launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = ImportExportManager.exportSnippets(context, snippets, it)
                result.onSuccess {
                    Toast.makeText(context, "Snippets exported successfully", Toast.LENGTH_SHORT).show()
                }.onFailure { error ->
                    Toast.makeText(context, "Export failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Import launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val result = ImportExportManager.importSnippets(context, it)
                result.onSuccess { importedSnippets ->
                    // Insert all imported snippets
                    importedSnippets.forEach { snippet ->
                        viewModel.insertSnippet(snippet.trigger, snippet.expansion)
                    }
                    Toast.makeText(
                        context,
                        "Imported ${importedSnippets.size} snippets",
                        Toast.LENGTH_SHORT
                    ).show()
                }.onFailure { error ->
                    Toast.makeText(context, "Import failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.SnippetList.route
    ) {
        composable(Screen.SnippetList.route) {
            SnippetListScreen(
                snippets = snippets,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onSnippetClick = { snippetId ->
                    navController.navigate(Screen.EditSnippet.createRoute(snippetId))
                },
                onSnippetDelete = viewModel::deleteSnippet,
                onSnippetToggle = viewModel::toggleSnippetEnabled,
                onAddClick = {
                    navController.navigate(Screen.AddSnippet.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.AddSnippet.route) {
            AddEditSnippetScreen(
                snippet = null,
                onSave = { trigger, expansion ->
                    viewModel.insertSnippet(trigger, expansion) {
                        navController.popBackStack()
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditSnippet.route,
            arguments = listOf(
                navArgument("snippetId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val snippetId = backStackEntry.arguments?.getLong("snippetId") ?: return@composable
            var snippet by remember { mutableStateOf<com.rrajath.expander.data.Snippet?>(null) }

            LaunchedEffect(snippetId) {
                viewModel.getSnippetById(snippetId) { result ->
                    snippet = result
                }
            }

            snippet?.let { currentSnippet ->
                AddEditSnippetScreen(
                    snippet = currentSnippet,
                    onSave = { trigger, expansion ->
                        val updatedSnippet = currentSnippet.copy(
                            trigger = trigger,
                            expansion = expansion
                        )
                        viewModel.updateSnippet(updatedSnippet) {
                            navController.popBackStack()
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExportClick = {
                    exportLauncher.launch(ImportExportManager.getDefaultExportFilename())
                },
                onImportClick = {
                    importLauncher.launch(arrayOf("application/json"))
                }
            )
        }
    }
}
