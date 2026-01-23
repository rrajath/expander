package com.rrajath.expander.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rrajath.expander.data.AppDatabase
import com.rrajath.expander.data.Snippet
import com.rrajath.expander.data.SnippetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SnippetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SnippetRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val snippets: StateFlow<List<Snippet>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.getAllSnippets()
            } else {
                repository.searchSnippets(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SnippetRepository(database.snippetDao())
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertSnippet(trigger: String, expansion: String, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val snippet = Snippet(
                trigger = trigger,
                expansion = expansion,
                isEnabled = true
            )
            val id = repository.insert(snippet)
            onComplete(id)
        }
    }

    fun updateSnippet(snippet: Snippet, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val updatedSnippet = snippet.copy(updatedAt = System.currentTimeMillis())
            repository.update(updatedSnippet)
            onComplete()
        }
    }

    fun deleteSnippet(snippet: Snippet) {
        viewModelScope.launch {
            repository.delete(snippet)
        }
    }

    fun getSnippetById(id: Long, onResult: (Snippet?) -> Unit) {
        viewModelScope.launch {
            val snippet = repository.getSnippetById(id)
            onResult(snippet)
        }
    }

    fun toggleSnippetEnabled(snippet: Snippet) {
        viewModelScope.launch {
            val updated = snippet.copy(
                isEnabled = !snippet.isEnabled,
                updatedAt = System.currentTimeMillis()
            )
            repository.update(updated)
        }
    }
}
