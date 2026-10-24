package com.example.notora.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notora.data.local.entity.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE noteId = :noteId ORDER BY createdAt ASC")
    fun getAttachmentsForNote(noteId: Long): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE noteId = :noteId ORDER BY createdAt ASC")
    suspend fun getAttachmentsForNoteSync(noteId: Long): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE id = :id")
    suspend fun getAttachmentById(id: Long): AttachmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

    @Query("DELETE FROM attachments WHERE noteId = :noteId")
    suspend fun deleteAttachmentsForNote(noteId: Long)
}
