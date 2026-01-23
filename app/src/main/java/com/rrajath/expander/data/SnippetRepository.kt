package com.rrajath.expander.data

import kotlinx.coroutines.flow.Flow

class SnippetRepository(private val snippetDao: SnippetDao) {

    fun getAllSnippets(): Flow<List<Snippet>> = snippetDao.getAllSnippets()

    fun getEnabledSnippets(): Flow<List<Snippet>> = snippetDao.getEnabledSnippets()

    suspend fun getSnippetById(id: Long): Snippet? = snippetDao.getSnippetById(id)

    fun searchSnippets(query: String): Flow<List<Snippet>> = snippetDao.searchSnippets(query)

    suspend fun insert(snippet: Snippet): Long = snippetDao.insert(snippet)

    suspend fun update(snippet: Snippet) = snippetDao.update(snippet)

    suspend fun delete(snippet: Snippet) = snippetDao.delete(snippet)

    suspend fun deleteById(id: Long) = snippetDao.deleteById(id)

    suspend fun deleteAll() = snippetDao.deleteAll()
}
