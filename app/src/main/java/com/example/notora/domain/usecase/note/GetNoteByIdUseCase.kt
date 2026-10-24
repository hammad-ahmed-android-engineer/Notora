package com.example.notora.domain.usecase.note

import com.example.notora.domain.model.Note
import com.example.notora.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe or fetch a single note by its unique identifier.
 */
class GetNoteByIdUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(noteId: Long): Flow<Note?> {
        return noteRepository.getNoteById(noteId)
    }

    suspend fun getSync(noteId: Long): Note? {
        return noteRepository.getNoteByIdSync(noteId)
    }
}
