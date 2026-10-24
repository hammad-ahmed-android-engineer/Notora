package com.example.notora.domain.repository

import com.example.notora.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun saveTag(name: String): Long
    suspend fun deleteTag(id: Long)
}
