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
import com.example.notora.domain.model.Attachment
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.ChecklistItem
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteType
import com.example.notora.domain.model.Tag

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
            category = category?.let { Category(id = it.id, name = it.name, colorHex = it.colorHex, iconName = it.iconName, displayOrder = it.displayOrder, isArchived = it.isArchived) },
            tags = tags.map { Tag(id = it.id, name = it.name) },
            checklistItems = checklistItems.sortedBy { it.displayOrder }.map { ChecklistItem(id = it.id, noteId = it.noteId, text = it.text, isCompleted = it.isCompleted, displayOrder = it.displayOrder) },
            attachments = attachments.map { Attachment(id = it.id, noteId = it.noteId, uri = it.uri, mimeType = it.mimeType, displayName = it.displayName, fileSizeBytes = it.fileSizeBytes, createdAt = it.createdAt) },
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
