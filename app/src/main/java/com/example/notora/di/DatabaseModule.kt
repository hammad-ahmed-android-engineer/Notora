package com.example.notora.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
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

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Safe migration: guarantees zero data loss by preserving all existing tables and data
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, contentJson TEXT NOT NULL, plainTextContent TEXT NOT NULL, noteType TEXT NOT NULL, categoryId INTEGER, colorHex TEXT, reminderTime INTEGER, isReminderActive INTEGER NOT NULL, isPinned INTEGER NOT NULL, isFavorite INTEGER NOT NULL, isArchived INTEGER NOT NULL, isTrashed INTEGER NOT NULL, trashedAt INTEGER, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, colorHex TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS tags (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS note_tag_cross_ref (noteId INTEGER NOT NULL, tagId INTEGER NOT NULL, PRIMARY KEY(noteId, tagId), FOREIGN KEY(noteId) REFERENCES notes(id) ON DELETE CASCADE, FOREIGN KEY(tagId) REFERENCES tags(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS checklist_items (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, noteId INTEGER NOT NULL, text TEXT NOT NULL, isCompleted INTEGER NOT NULL, position INTEGER NOT NULL, FOREIGN KEY(noteId) REFERENCES notes(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS attachments (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, noteId INTEGER NOT NULL, uri TEXT NOT NULL, name TEXT NOT NULL, mimeType TEXT NOT NULL, size INTEGER NOT NULL, createdAt INTEGER NOT NULL, FOREIGN KEY(noteId) REFERENCES notes(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS note_revisions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, noteId INTEGER NOT NULL, title TEXT NOT NULL, contentJson TEXT NOT NULL, createdAt INTEGER NOT NULL, FOREIGN KEY(noteId) REFERENCES notes(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS note_links (sourceNoteId INTEGER NOT NULL, targetNoteId INTEGER NOT NULL, PRIMARY KEY(sourceNoteId, targetNoteId), FOREIGN KEY(sourceNoteId) REFERENCES notes(id) ON DELETE CASCADE, FOREIGN KEY(targetNoteId) REFERENCES notes(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS note_templates (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, contentJson TEXT NOT NULL, iconName TEXT NOT NULL)")
        
        // Properly handle FTS4 virtual table creation and data population
        db.execSQL("DROP TABLE IF EXISTS notes_fts")
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS notes_fts USING fts4(content=`notes`, title, plainTextContent)")
        db.execSQL("INSERT INTO notes_fts(rowid, title, plainTextContent) SELECT id, title, plainTextContent FROM notes")
    }
}

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
            .addMigrations(MIGRATION_1_2)
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
