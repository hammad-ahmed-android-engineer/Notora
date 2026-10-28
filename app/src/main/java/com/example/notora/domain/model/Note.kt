package com.example.notora.domain.model

import com.example.notora.core.utils.AppUtils
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Long = 0,
    val title: String = "",
    val contentJson: String = "",
    val plainTextContent: String = "",
    val noteType: NoteType = NoteType.STANDARD,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val category: Category? = null,
    val tags: List<Tag> = emptyList(),
    val checklistItems: List<ChecklistItem> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val linkedNoteIds: List<Long> = emptyList(),
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
) {
    val displayTitle: String
        get() = title.ifBlank {
            AppUtils.extractFirstLine(plainTextContent, maxLength = 40, fallback = "Untitled Note")
        }

    val previewText: String
        get() = plainTextContent.trim()

    val checklistProgress: Float
        get() = if (checklistItems.isEmpty()) 0f
        else checklistItems.count { it.isCompleted }.toFloat() / checklistItems.size

    val checklistCompletedCount: Int
        get() = checklistItems.count { it.isCompleted }
}
