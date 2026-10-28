package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.notora.domain.model.ChecklistItem

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteId")]
)
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val text: String,
    val isCompleted: Boolean = false,
    val displayOrder: Int = 0
)

fun ChecklistItemEntity.toDomainModel(): ChecklistItem = ChecklistItem(
    id = id,
    text = text,
    isCompleted = isCompleted,
    displayOrder = displayOrder
)

fun ChecklistItem.toEntity(noteId: Long): ChecklistItemEntity = ChecklistItemEntity(
    id = if (id == 0L) 0 else id,
    noteId = noteId,
    text = text,
    isCompleted = isCompleted,
    displayOrder = displayOrder
)
