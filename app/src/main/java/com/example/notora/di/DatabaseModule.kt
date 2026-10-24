package com.example.notora.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notora.data.local.NoteNestDatabase
import com.example.notora.data.local.dao.AttachmentDao
import com.example.notora.data.local.dao.CategoryDao
import com.example.notora.data.local.dao.ChecklistDao
import com.example.notora.data.local.dao.NoteDao
import com.example.notora.data.local.dao.RevisionDao
import com.example.notora.data.local.dao.TagDao
import com.example.notora.data.local.dao.TemplateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NoteNestDatabase {
        return Room.databaseBuilder(
            context,
            NoteNestDatabase::class.java,
            "notora_notenest.db"
        )
            // NEVER use fallbackToDestructiveMigration() - data must NEVER be deleted on schema changes or errors
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) // High-durability WAL mode protects data
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    // Ensure foreign keys are enabled and run safe integrity check
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            })
            .build()
    }

    @Provides
    fun provideNoteDao(database: NoteNestDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideCategoryDao(database: NoteNestDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTagDao(database: NoteNestDatabase): TagDao = database.tagDao()

    @Provides
    fun provideChecklistDao(database: NoteNestDatabase): ChecklistDao = database.checklistDao()

    @Provides
    fun provideAttachmentDao(database: NoteNestDatabase): AttachmentDao = database.attachmentDao()

    @Provides
    fun provideRevisionDao(database: NoteNestDatabase): RevisionDao = database.revisionDao()

    @Provides
    fun provideTemplateDao(database: NoteNestDatabase): TemplateDao = database.templateDao()
}
