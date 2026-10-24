package com.example.notora.domain.model

/**
 * Represents a date-wise group of notes with a unique ID, human-readable section heading,
 * and the list of notes belonging to this date boundary.
 */
data class NoteDateGroup(
    val id: String,
    val title: String,
    val notes: List<Note>
)
