package com.example.notora.domain.usecase.note

import com.example.notora.domain.model.Note
import com.example.notora.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all archived notes.
 */
class GetArchivedNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return noteRepository.getArchivedNotes()
    }
}
