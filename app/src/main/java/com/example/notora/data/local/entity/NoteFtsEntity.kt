package com.example.notora.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "notes_fts")
@Fts4(contentEntity = NoteEntity::class)
data class NoteFtsEntity(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Long,
    val title: String,
    val plainTextContent: String
)
