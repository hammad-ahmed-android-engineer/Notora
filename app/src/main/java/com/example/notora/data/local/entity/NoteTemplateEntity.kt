package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_templates")
data class NoteTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val noteType: String = "STANDARD",
    val titleTemplate: String = "",
    val contentJsonTemplate: String = "",
    val categoryName: String? = null,
    val isDefault: Boolean = false
)
