package com.example.notora.domain.usecase.template

import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteTemplate
import com.example.notora.domain.repository.NoteRepository
import com.example.notora.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all note templates and ensure default templates are seeded.
 */
class GetTemplatesUseCase @Inject constructor(
    private val templateRepository: TemplateRepository
) {
    suspend fun seedDefaultsIfEmpty() {
        templateRepository.seedDefaultTemplatesIfEmpty()
    }

    operator fun invoke(): Flow<List<NoteTemplate>> {
        return templateRepository.getAllTemplates()
    }
}

/**
 * Use case to create a new note instantiated from a template.
 */
class CreateNoteFromTemplateUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(template: NoteTemplate): Long {
        val plainText = RichTextSerializer.fromJson(template.contentJsonTemplate).text
        val newNote = Note(
            id = 0,
            title = template.titleTemplate,
            contentJson = template.contentJsonTemplate,
            plainTextContent = plainText,
            noteType = template.noteType,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return noteRepository.saveNote(newNote)
    }
}

/**
 * Use case to delete a template.
 */
class DeleteTemplateUseCase @Inject constructor(
    private val templateRepository: TemplateRepository
) {
    suspend operator fun invoke(templateId: Long) {
        templateRepository.deleteTemplate(templateId)
    }
}
