package com.rrajath.expander.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {
    @Query("SELECT * FROM snippets ORDER BY updatedAt DESC")
    fun getAllSnippets(): Flow<List<Snippet>>

    @Query("SELECT * FROM snippets WHERE isEnabled = 1")
    fun getEnabledSnippets(): Flow<List<Snippet>>

    @Query("SELECT * FROM snippets WHERE id = :id")
    suspend fun getSnippetById(id: Long): Snippet?

    @Query("SELECT * FROM snippets WHERE trigger LIKE '%' || :query || '%' OR expansion LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchSnippets(query: String): Flow<List<Snippet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snippet: Snippet): Long

    @Update
    suspend fun update(snippet: Snippet)

    @Delete
    suspend fun delete(snippet: Snippet)

    @Query("DELETE FROM snippets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM snippets")
    suspend fun deleteAll()
}
