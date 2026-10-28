package com.example.notora.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChecklistItem(
    val id: Long = 0,
    val noteId: Long = 0,
    val text: String,
    val isCompleted: Boolean = false,
    val displayOrder: Int = 0
)
