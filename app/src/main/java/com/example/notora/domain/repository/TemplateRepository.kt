package com.example.notora.domain.repository

import com.example.notora.domain.model.NoteTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for note templates.
 */
interface TemplateRepository {
    fun getAllTemplates(): Flow<List<NoteTemplate>>
    suspend fun seedDefaultTemplatesIfEmpty()
    suspend fun saveTemplate(template: NoteTemplate): Long
    suspend fun deleteTemplate(id: Long)
}
