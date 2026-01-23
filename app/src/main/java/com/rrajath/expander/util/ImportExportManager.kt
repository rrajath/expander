package com.rrajath.expander.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rrajath.expander.data.Snippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object ImportExportManager {

    private val gson = Gson()

    /**
     * Export snippets to a JSON file
     */
    suspend fun exportSnippets(
        context: Context,
        snippets: List<Snippet>,
        uri: Uri
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    val json = gson.toJson(snippets)
                    writer.write(json)
                }
            } ?: throw Exception("Failed to open output stream")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Import snippets from a JSON file
     */
    suspend fun importSnippets(
        context: Context,
        uri: Uri
    ): Result<List<Snippet>> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val json = reader.readText()
                    val type = object : TypeToken<List<Snippet>>() {}.type
                    val snippets: List<Snippet> = gson.fromJson(json, type)

                    // Reset IDs to avoid conflicts
                    val resetSnippets = snippets.map { it.copy(id = 0) }
                    Result.success(resetSnippets)
                }
            } ?: throw Exception("Failed to open input stream")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a default filename for export
     */
    fun getDefaultExportFilename(): String {
        val timestamp = System.currentTimeMillis()
        return "expander_snippets_$timestamp.json"
    }
}
