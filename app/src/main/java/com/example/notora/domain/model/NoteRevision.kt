package com.example.notora.domain.model

data class NoteRevision(
    val id: Long = 0,
    val noteId: Long = 0,
    val title: String,
    val contentJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
