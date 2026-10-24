package com.example.notora.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notora.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId ORDER BY displayOrder ASC")
    fun getChecklistItemsForNote(noteId: Long): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId ORDER BY displayOrder ASC")
    suspend fun getChecklistItemsForNoteSync(noteId: Long): List<ChecklistItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateChecklistItem(item: ChecklistItemEntity)

    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_items WHERE noteId = :noteId")
    suspend fun deleteChecklistItemsForNote(noteId: Long)
}
