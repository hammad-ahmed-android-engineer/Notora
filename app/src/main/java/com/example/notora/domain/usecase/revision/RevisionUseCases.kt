package com.example.notora.domain.usecase.revision

import com.example.notora.common.util.AppUtils
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.model.NoteRevision
import com.example.notora.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe revision snapshots for a given note.
 */
class GetRevisionsUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(noteId: Long): Flow<List<NoteRevision>> {
        return noteRepository.getRevisions(noteId)
    }
}

/**
 * Use case to restore a note revision snapshot.
 */
class RestoreRevisionUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long, revision: NoteRevision): Boolean {
        val currentNote = noteRepository.getNoteByIdSync(noteId) ?: return false
        val doc = RichTextSerializer.fromJson(revision.contentJson)
        val words = AppUtils.calculateWordCount(doc.text)
        val updatedNote = currentNote.copy(
            title = revision.title,
            contentJson = revision.contentJson,
            plainTextContent = doc.text,
            wordCount = words,
            charCount = AppUtils.calculateCharCount(doc.text),
            readingTimeMinutes = AppUtils.calculateReadingTimeMinutes(words),
            updatedAt = System.currentTimeMillis()
        )
        noteRepository.saveNote(updatedNote)
        return true
    }
}
