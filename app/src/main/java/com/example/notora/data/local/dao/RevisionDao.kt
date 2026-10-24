package com.example.notora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notora.data.local.entity.NoteRevisionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RevisionDao {
    @Query("SELECT * FROM note_revisions WHERE noteId = :noteId ORDER BY createdAt DESC")
    fun getRevisionsForNote(noteId: Long): Flow<List<NoteRevisionEntity>>

    @Query("SELECT * FROM note_revisions WHERE noteId = :noteId ORDER BY createdAt DESC")
    suspend fun getRevisionsForNoteSync(noteId: Long): List<NoteRevisionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(revision: NoteRevisionEntity): Long

    @Query("DELETE FROM note_revisions WHERE noteId = :noteId")
    suspend fun deleteRevisionsForNote(noteId: Long)
}
