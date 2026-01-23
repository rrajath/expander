package com.rrajath.expander.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rrajath.expander.data.Snippet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSnippetScreen(
    snippet: Snippet?,
    onSave: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var trigger by remember { mutableStateOf(snippet?.trigger ?: "") }
    var expansion by remember { mutableStateOf(snippet?.expansion ?: "") }
    var triggerError by remember { mutableStateOf<String?>(null) }
    var expansionError by remember { mutableStateOf<String?>(null) }

    val isEditMode = snippet != null
    val title = if (isEditMode) "Edit Snippet" else "Add Snippet"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trigger field
            OutlinedTextField(
                value = trigger,
                onValueChange = {
                    trigger = it
                    triggerError = null
                },
                label = { Text("Trigger") },
                placeholder = { Text("e.g., !email") },
                modifier = Modifier.fillMaxWidth(),
                isError = triggerError != null,
                supportingText = {
                    if (triggerError != null) {
                        Text(
                            text = triggerError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("The shortcut that triggers expansion")
                    }
                },
                singleLine = true
            )

            // Expansion field
            OutlinedTextField(
                value = expansion,
                onValueChange = {
                    expansion = it
                    expansionError = null
                },
                label = { Text("Expansion") },
                placeholder = { Text("e.g., john.doe@example.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                isError = expansionError != null,
                supportingText = {
                    if (expansionError != null) {
                        Text(
                            text = expansionError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("The text that replaces the trigger")
                    }
                },
                minLines = 3,
                maxLines = 10
            )

            // Dynamic snippets help card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Dynamic Placeholders",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = """
                            • {{date}} - Current date (yyyy-MM-dd)
                            • {{time}} - Current time (HH:mm:ss)
                            • {{datetime}} - Date and time
                            • {{day}} - Day (Mon, Tue, Wed)
                            • {{day_long}} - Day (Monday, Tuesday)
                            • {{month}} - Month (Jan, Feb, Mar)
                            • {{month_long}} - Month (January, February)
                            • {{year}} - Year (2026)
                            • {{date:dd/MM/yyyy}} - Custom format
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    // Validation
                    var hasError = false

                    if (trigger.isBlank()) {
                        triggerError = "Trigger cannot be empty"
                        hasError = true
                    }

                    if (expansion.isBlank()) {
                        expansionError = "Expansion cannot be empty"
                        hasError = true
                    }

                    if (!hasError) {
                        onSave(trigger.trim(), expansion)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(if (isEditMode) "Save Changes" else "Add Snippet")
            }
        }
    }
}
