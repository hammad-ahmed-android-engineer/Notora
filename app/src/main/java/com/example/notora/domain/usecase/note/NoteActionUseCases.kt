package com.example.notora.domain.usecase.note

import com.example.notora.domain.model.Note
import com.example.notora.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case to toggle the pinned status of a note.
 */
class TogglePinUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        noteRepository.togglePin(noteId)
    }
}

/**
 * Use case to toggle the favorite status of a note.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        noteRepository.toggleFavorite(noteId)
    }
}

/**
 * Use case to move a note to/from archive.
 */
class ArchiveNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        noteRepository.archiveNote(noteId)
    }

    suspend fun unarchive(noteId: Long) {
        noteRepository.unarchiveNote(noteId)
    }
}

/**
 * Use case to soft-delete a note into trash.
 */
class TrashNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        noteRepository.softDeleteNote(noteId)
    }
}

/**
 * Use case to restore a soft-deleted note from trash.
 */
class RestoreNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        noteRepository.restoreNoteFromTrash(noteId)
    }
}

/**
 * Use case to permanently delete a note.
 */
class DeleteNotePermanentlyUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: Long) {
        noteRepository.deleteNote(noteId)
    }
}

/**
 * Use case to purge all notes currently in the trash.
 */
class EmptyTrashUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke() {
        noteRepository.emptyTrash()
    }
}

/**
 * Use case to duplicate an existing note.
 */
class DuplicateNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Long {
        return noteRepository.duplicateNote(note)
    }
}
