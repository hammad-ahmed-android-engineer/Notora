package com.example.notora.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.notora.data.local.entity.AttachmentEntity
import com.example.notora.data.local.entity.CategoryEntity
import com.example.notora.data.local.entity.ChecklistItemEntity
import com.example.notora.data.local.entity.NoteEntity
import com.example.notora.data.local.entity.NoteTagCrossRef
import com.example.notora.data.local.entity.TagEntity
import com.example.notora.data.local.entity.toDomainModel
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteType

data class NoteWithDetails(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "noteId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val checklistItems: List<ChecklistItemEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val attachments: List<AttachmentEntity>
) {
    fun toDomainModel(linkedNoteIds: List<Long> = emptyList()): Note {
        return Note(
            id = note.id,
            title = note.title,
            contentJson = note.contentJson,
            plainTextContent = note.plainTextContent,
            noteType = try { NoteType.valueOf(note.noteType) } catch (_: Exception) { NoteType.STANDARD },
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            category = category?.toDomainModel(),
            tags = tags.map { it.toDomainModel() },
            checklistItems = checklistItems.sortedBy { it.displayOrder }.map { it.toDomainModel() },
            attachments = attachments.map { it.toDomainModel() },
            linkedNoteIds = linkedNoteIds,
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
    }
}
