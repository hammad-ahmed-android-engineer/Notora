package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_links",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceNoteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetNoteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sourceNoteId"), Index("targetNoteId")]
)
data class NoteLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceNoteId: Long,
    val targetNoteId: Long
)
