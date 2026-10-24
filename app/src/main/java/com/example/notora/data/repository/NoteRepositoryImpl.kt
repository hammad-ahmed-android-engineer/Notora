package com.example.notora.data.repository

import com.example.notora.data.local.dao.AttachmentDao
import com.example.notora.data.local.dao.ChecklistDao
import com.example.notora.data.local.dao.NoteDao
import com.example.notora.data.local.dao.RevisionDao
import com.example.notora.data.local.entity.AttachmentEntity
import com.example.notora.data.local.entity.ChecklistItemEntity
import com.example.notora.data.local.entity.NoteEntity
import com.example.notora.data.local.entity.NoteRevisionEntity
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteFilter
import com.example.notora.domain.model.NoteRevision
import com.example.notora.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val checklistDao: ChecklistDao,
    private val attachmentDao: AttachmentDao,
    private val revisionDao: RevisionDao
) : NoteRepository {

    override fun getActiveNotes(): Flow<List<Note>> {
        return noteDao.getActiveNotes()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getArchivedNotes(): Flow<List<Note>> {
        return noteDao.getArchivedNotes()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getTrashedNotes(): Flow<List<Note>> {
        return noteDao.getTrashedNotes()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getFavoriteNotes(): Flow<List<Note>> {
        return noteDao.getFavoriteNotes()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getPinnedNotes(): Flow<List<Note>> {
        return noteDao.getPinnedNotes()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getNotesByCategory(categoryId: Long): Flow<List<Note>> {
        return noteDao.getNotesByCategory(categoryId)
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return noteDao.getNoteById(id)
            .map { it?.toDomainModel() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getNoteByIdSync(id: Long): Note? {
        return noteDao.getNoteByIdSync(id)?.toDomainModel()
    }

    override fun searchNotes(filter: NoteFilter): Flow<List<Note>> {
        return noteDao.searchNotes(
            query = filter.query,
            categoryId = filter.categoryId,
            noteType = filter.noteType?.name,
            colorHex = filter.colorHex,
            isPinnedOnly = if (filter.isPinnedOnly) 1 else 0,
            isFavoriteOnly = if (filter.isFavoriteOnly) 1 else 0,
            includeArchived = if (filter.isArchivedOnly) 1 else 0,
            includeTrashed = if (filter.isTrashedOnly) 1 else 0
        )
        .map { list -> list.map { it.toDomainModel() } }
        .flowOn(Dispatchers.IO)
    }

    override suspend fun saveNote(note: Note): Long {
        val noteEntity = NoteEntity(
            id = note.id,
            title = note.title,
            contentJson = note.contentJson,
            plainTextContent = note.plainTextContent,
            noteType = note.noteType.name,
            createdAt = note.createdAt,
            updatedAt = System.currentTimeMillis(),
            categoryId = note.category?.id,
            colorHex = note.colorHex,
            isPinned = note.isPinned,
            isArchived = note.isArchived,
            isTrashed = note.isTrashed,
            trashedAt = note.trashedAt,
            isFavorite = note.isFavorite,
            coverImageUri = note.coverImageUri,
            sourceUrl = note.sourceUrl,
            locationLabel = note.locationLabel,
            mood = note.mood,
            reminderTime = note.reminderTime,
            reminderRepeat = note.reminderRepeat,
            isReminderActive = note.isReminderActive,
            wordCount = note.wordCount,
            charCount = note.charCount,
            readingTimeMinutes = note.readingTimeMinutes
        )

        val noteId = if (note.id == 0L) {
            noteDao.insertNote(noteEntity)
        } else {
            noteDao.updateNote(noteEntity)
            note.id
        }

        // Save tags
        val tagIds = note.tags.map { it.id }
        noteDao.setNoteTags(noteId, tagIds)

        // Save checklist items
        checklistDao.deleteChecklistItemsForNote(noteId)
        val checklistEntities = note.checklistItems.map {
            ChecklistItemEntity(
                id = if (it.id == 0L) 0 else it.id,
                noteId = noteId,
                text = it.text,
                isCompleted = it.isCompleted,
                displayOrder = it.displayOrder
            )
        }
        checklistDao.insertChecklistItems(checklistEntities)

        // Save attachments
        attachmentDao.deleteAttachmentsForNote(noteId)
        note.attachments.forEach {
            attachmentDao.insertAttachment(
                AttachmentEntity(
                    id = if (it.id == 0L) 0 else it.id,
                    noteId = noteId,
                    uri = it.uri,
                    mimeType = it.mimeType,
                    displayName = it.displayName,
                    fileSizeBytes = it.fileSizeBytes,
                    createdAt = it.createdAt
                )
            )
        }

        return noteId
    }

    override suspend fun deleteNote(id: Long) {
        noteDao.deleteNoteById(id)
    }

    override suspend fun softDeleteNote(id: Long) {
        val noteWithDetails = noteDao.getNoteByIdSync(id)
        noteWithDetails?.note?.let {
            noteDao.updateNote(it.copy(isTrashed = true, trashedAt = System.currentTimeMillis()))
        }
    }

    override suspend fun restoreNoteFromTrash(id: Long) {
        val noteWithDetails = noteDao.getNoteByIdSync(id)
        noteWithDetails?.note?.let {
            noteDao.updateNote(it.copy(isTrashed = false, trashedAt = null))
        }
    }

    override suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    override suspend fun togglePin(id: Long) {
        val noteWithDetails = noteDao.getNoteByIdSync(id)
        noteWithDetails?.note?.let {
            noteDao.updateNote(it.copy(isPinned = !it.isPinned))
        }
    }

    override suspend fun toggleFavorite(id: Long) {
        val noteWithDetails = noteDao.getNoteByIdSync(id)
        noteWithDetails?.note?.let {
            noteDao.updateNote(it.copy(isFavorite = !it.isFavorite))
        }
    }

    override suspend fun archiveNote(id: Long) {
        val noteWithDetails = noteDao.getNoteByIdSync(id)
        noteWithDetails?.note?.let {
            noteDao.updateNote(it.copy(isArchived = true))
        }
    }

    override suspend fun unarchiveNote(id: Long) {
        val noteWithDetails = noteDao.getNoteByIdSync(id)
        noteWithDetails?.note?.let {
            noteDao.updateNote(it.copy(isArchived = false))
        }
    }

    override suspend fun duplicateNote(note: Note): Long {
        val copy = note.copy(
            id = 0,
            title = "${note.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return saveNote(copy)
    }

    override suspend fun createRevision(noteId: Long, title: String, contentJson: String) {
        revisionDao.insertRevision(
            NoteRevisionEntity(
                noteId = noteId,
                title = title,
                contentJson = contentJson,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override fun getRevisions(noteId: Long): Flow<List<NoteRevision>> {
        return revisionDao.getRevisionsForNote(noteId)
            .map { list ->
                list.map {
                    NoteRevision(
                        id = it.id,
                        noteId = it.noteId,
                        title = it.title,
                        contentJson = it.contentJson,
                        createdAt = it.createdAt
                    )
                }
            }
            .flowOn(Dispatchers.IO)
    }
}
