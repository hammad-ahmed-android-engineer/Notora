package com.example.notora.domain.model

data class NoteTemplate(
    val id: Long = 0,
    val name: String,
    val description: String,
    val noteType: NoteType = NoteType.STANDARD,
    val titleTemplate: String = "",
    val contentJsonTemplate: String = "",
    val categoryName: String? = null,
    val isDefault: Boolean = false
)
