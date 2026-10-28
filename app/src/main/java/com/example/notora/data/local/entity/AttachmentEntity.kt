package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.notora.domain.model.Attachment

@Entity(
    tableName = "attachments",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteId")]
)
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val uri: String,
    val mimeType: String,
    val displayName: String,
    val fileSizeBytes: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

fun AttachmentEntity.toDomainModel(): Attachment = Attachment(
    id = id,
    uri = uri,
    mimeType = mimeType,
    displayName = displayName,
    fileSizeBytes = fileSizeBytes,
    createdAt = createdAt
)

fun Attachment.toEntity(noteId: Long): AttachmentEntity = AttachmentEntity(
    id = if (id == 0L) 0 else id,
    noteId = noteId,
    uri = uri,
    mimeType = mimeType,
    displayName = displayName,
    fileSizeBytes = fileSizeBytes,
    createdAt = createdAt
)
