package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notora.domain.model.NoteTemplate
import com.example.notora.domain.model.NoteType

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

fun NoteTemplateEntity.toDomainModel(): NoteTemplate = NoteTemplate(
    id = id,
    name = name,
    description = description,
    noteType = try { NoteType.valueOf(noteType) } catch (_: Exception) { NoteType.STANDARD },
    titleTemplate = titleTemplate,
    contentJsonTemplate = contentJsonTemplate,
    categoryName = categoryName,
    isDefault = isDefault
)

fun NoteTemplate.toEntity(): NoteTemplateEntity = NoteTemplateEntity(
    id = id,
    name = name,
    description = description,
    noteType = noteType.name,
    titleTemplate = titleTemplate,
    contentJsonTemplate = contentJsonTemplate,
    categoryName = categoryName,
    isDefault = isDefault
)
