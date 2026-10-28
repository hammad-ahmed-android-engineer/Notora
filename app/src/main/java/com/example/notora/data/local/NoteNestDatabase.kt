package com.example.notora.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notora.data.local.dao.AttachmentDao
import com.example.notora.data.local.dao.CategoryDao
import com.example.notora.data.local.dao.ChecklistDao
import com.example.notora.data.local.dao.NoteDao
import com.example.notora.data.local.dao.RevisionDao
import com.example.notora.data.local.dao.TagDao
import com.example.notora.data.local.dao.TemplateDao
import com.example.notora.data.local.entity.AttachmentEntity
import com.example.notora.data.local.entity.CategoryEntity
import com.example.notora.data.local.entity.ChecklistItemEntity
import com.example.notora.data.local.entity.NoteEntity
import com.example.notora.data.local.entity.NoteFtsEntity
import com.example.notora.data.local.entity.NoteLinkEntity
import com.example.notora.data.local.entity.NoteRevisionEntity
import com.example.notora.data.local.entity.NoteTagCrossRef
import com.example.notora.data.local.entity.NoteTemplateEntity
import com.example.notora.data.local.entity.TagEntity

@Database(
    entities = [
        NoteEntity::class,
        NoteFtsEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        NoteTagCrossRef::class,
        ChecklistItemEntity::class,
        AttachmentEntity::class,
        NoteRevisionEntity::class,
        NoteLinkEntity::class,
        NoteTemplateEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NoteNestDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun revisionDao(): RevisionDao
    abstract fun templateDao(): TemplateDao
}
