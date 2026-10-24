package com.example.notora.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notora.data.local.entity.NoteTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM note_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<NoteTemplateEntity>>

    @Query("SELECT * FROM note_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): NoteTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: NoteTemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: NoteTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: NoteTemplateEntity)
}
