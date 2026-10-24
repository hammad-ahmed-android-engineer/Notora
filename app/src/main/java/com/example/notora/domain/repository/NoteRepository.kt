package com.example.notora.domain.repository

import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteFilter
import com.example.notora.domain.model.NoteRevision
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getActiveNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getTrashedNotes(): Flow<List<Note>>
    fun getFavoriteNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getNotesByCategory(categoryId: Long): Flow<List<Note>>
    fun getNoteById(id: Long): Flow<Note?>
    suspend fun getNoteByIdSync(id: Long): Note?
    fun searchNotes(filter: NoteFilter): Flow<List<Note>>
    suspend fun saveNote(note: Note): Long
    suspend fun deleteNote(id: Long)
    suspend fun softDeleteNote(id: Long)
    suspend fun restoreNoteFromTrash(id: Long)
    suspend fun emptyTrash()
    suspend fun togglePin(id: Long)
    suspend fun toggleFavorite(id: Long)
    suspend fun archiveNote(id: Long)
    suspend fun unarchiveNote(id: Long)
    suspend fun duplicateNote(note: Note): Long
    suspend fun createRevision(noteId: Long, title: String, contentJson: String)
    fun getRevisions(noteId: Long): Flow<List<NoteRevision>>
}
