package com.rrajath.expander.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "snippets")
data class Snippet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trigger: String,
    val expansion: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
