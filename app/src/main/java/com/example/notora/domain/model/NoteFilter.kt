package com.example.notora.domain.model

data class NoteFilter(
    val query: String = "",
    val categoryId: Long? = null,
    val tagId: Long? = null,
    val noteType: NoteType? = null,
    val colorHex: String? = null,
    val isPinnedOnly: Boolean = false,
    val isFavoriteOnly: Boolean = false,
    val isArchivedOnly: Boolean = false,
    val isTrashedOnly: Boolean = false,
    val hasReminderOnly: Boolean = false,
    val hasAttachmentOnly: Boolean = false,
    val hasChecklistOnly: Boolean = false,
    val sortOption: NoteSortOption = NoteSortOption.MODIFIED_DESC
)
