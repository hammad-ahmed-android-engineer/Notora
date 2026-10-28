package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.notora.domain.model.Note

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("categoryId"),
        Index("isPinned"),
        Index("isArchived"),
        Index("isTrashed"),
        Index("isFavorite"),
        Index("updatedAt"),
        Index("createdAt")
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val contentJson: String = "",
    val plainTextContent: String = "",
    val noteType: String = "STANDARD",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val categoryId: Long? = null,
    val colorHex: String = "#FFFFFF",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false,
    val trashedAt: Long? = null,
    val isFavorite: Boolean = false,
    val coverImageUri: String? = null,
    val sourceUrl: String? = null,
    val locationLabel: String? = null,
    val mood: String? = null,
    val reminderTime: Long? = null,
    val reminderRepeat: String = "NONE",
    val isReminderActive: Boolean = false,
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val readingTimeMinutes: Int = 0
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    contentJson = contentJson,
    plainTextContent = plainTextContent,
    noteType = noteType.name,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
    categoryId = category?.id,
    colorHex = colorHex,
    isPinned = isPinned,
    isArchived = isArchived,
    isTrashed = isTrashed,
    trashedAt = trashedAt,
    isFavorite = isFavorite,
    coverImageUri = coverImageUri,
    sourceUrl = sourceUrl,
    locationLabel = locationLabel,
    mood = mood,
    reminderTime = reminderTime,
    reminderRepeat = reminderRepeat,
    isReminderActive = isReminderActive,
    wordCount = wordCount,
    charCount = charCount,
    readingTimeMinutes = readingTimeMinutes
)
