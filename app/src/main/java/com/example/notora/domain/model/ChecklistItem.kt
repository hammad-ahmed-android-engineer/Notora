package com.example.notora.domain.model

data class ChecklistItem(
    val id: Long = 0,
    val noteId: Long = 0,
    val text: String,
    val isCompleted: Boolean = false,
    val displayOrder: Int = 0
)
