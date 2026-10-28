package com.example.notora.data.backup

import android.content.Context
import android.net.Uri
import com.example.notora.data.local.dao.CategoryDao
import com.example.notora.data.local.dao.NoteDao
import com.example.notora.data.local.dao.TagDao
import com.example.notora.data.local.entity.NoteEntity
import com.example.notora.data.local.entity.TagEntity
import com.example.notora.data.local.entity.toDomainModel
import com.example.notora.data.local.entity.toEntity
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.Tag
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data container for complete application backup state.
 */
@Serializable
data class NoteNestBackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: List<Note> = emptyList(),
    val categories: List<Category> = emptyList(),
    val tags: List<Tag> = emptyList()
)

/**
 * Result metrics returned after a backup import operation.
 */
data class BackupImportResult(
    val success: Boolean,
    val importedNotesCount: Int = 0,
    val importedCategoriesCount: Int = 0,
    val importedTagsCount: Int = 0,
    val errorMessage: String? = null
)

/**
 * Coordinates persistent storage backup export and import, as well as single-file note IO.
 */
@Singleton
class BackupRestoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao,
    private val noteFileExporter: NoteFileExporter
) {
    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    /**
     * Exports all active and archived notes, categories, and tags to a JSON file via [uri].
     */
    suspend fun exportBackupToJson(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val activeNotes = noteDao.getActiveNotes().first().map { it.toDomainModel() }
            val archivedNotes = noteDao.getArchivedNotes().first().map { it.toDomainModel() }
            val allNotes = activeNotes + archivedNotes

            val categories = categoryDao.getAllCategories().first().map { it.toDomainModel() }
            val tags = tagDao.getAllTags().first().map { it.toDomainModel() }

            val backupData = NoteNestBackupData(
                version = 1,
                timestamp = System.currentTimeMillis(),
                notes = allNotes,
                categories = categories,
                tags = tags
            )

            val jsonString = jsonFormatter.encodeToString(backupData)
            val outputStream: OutputStream = context.contentResolver.openOutputStream(uri)
                ?: return@runCatching false

            outputStream.use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(jsonString)
                }
            }
            true
        }.getOrDefault(false)
    }

    /**
     * Imports categories, tags, and notes from a JSON backup file at [uri] after strict syntax and schema validation.
     */
    suspend fun importBackupFromJson(uri: Uri): BackupImportResult = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val jsonString = inputStream?.use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    reader.readText()
                }
            }?.trim() ?: return@withContext BackupImportResult(false, errorMessage = "Could not read the selected file.")

            if (jsonString.isBlank()) {
                return@withContext BackupImportResult(false, errorMessage = "The selected file is empty.")
            }

            val backupData = try {
                jsonFormatter.decodeFromString<NoteNestBackupData>(jsonString)
            } catch (e: Exception) {
                return@withContext BackupImportResult(
                    false,
                    errorMessage = "Invalid JSON file or incompatible backup schema: ${e.localizedMessage}"
                )
            }

            var importedCategories = 0
            var importedTags = 0
            var importedNotes = 0

            backupData.categories.forEach { cat: Category ->
                val existing = categoryDao.getCategoryByName(cat.name)
                if (existing == null) {
                    categoryDao.insertCategory(cat.toEntity())
                    importedCategories++
                }
            }

            backupData.tags.forEach { tag: Tag ->
                val existing = tagDao.getTagByName(tag.name)
                if (existing == null) {
                    tagDao.insertTag(TagEntity(name = tag.name))
                    importedTags++
                }
            }

            backupData.notes.forEach { note: Note ->
                val newNote = note.copy(id = 0)
                val catId = note.category?.let { categoryDao.getCategoryByName(it.name)?.id }
                val tagIds = note.tags.mapNotNull { tagDao.getTagByName(it.name)?.id }

                val noteEntity = NoteEntity(
                    title = newNote.title,
                    contentJson = newNote.contentJson,
                    plainTextContent = newNote.plainTextContent,
                    noteType = newNote.noteType.name,
                    createdAt = newNote.createdAt,
                    updatedAt = newNote.updatedAt,
                    categoryId = catId,
                    colorHex = newNote.colorHex,
                    isPinned = newNote.isPinned,
                    isArchived = newNote.isArchived,
                    isTrashed = newNote.isTrashed,
                    trashedAt = newNote.trashedAt,
                    isFavorite = newNote.isFavorite,
                    coverImageUri = newNote.coverImageUri,
                    sourceUrl = newNote.sourceUrl,
                    locationLabel = newNote.locationLabel,
                    mood = newNote.mood,
                    reminderTime = newNote.reminderTime,
                    reminderRepeat = newNote.reminderRepeat,
                    isReminderActive = newNote.isReminderActive,
                    wordCount = newNote.wordCount,
                    charCount = newNote.charCount,
                    readingTimeMinutes = newNote.readingTimeMinutes
                )

                val newNoteId = noteDao.insertNote(noteEntity)
                noteDao.setNoteTags(newNoteId, tagIds)
                importedNotes++
            }

            BackupImportResult(
                success = true,
                importedNotesCount = importedNotes,
                importedCategoriesCount = importedCategories,
                importedTagsCount = importedTags
            )
        } catch (e: Exception) {
            BackupImportResult(false, errorMessage = e.localizedMessage ?: "Import failed due to an unexpected error.")
        }
    }

    suspend fun exportNoteToFile(note: Note, uri: Uri, format: ExportFormat): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val outputText = noteFileExporter.formatNote(note, format)
            val outputStream: OutputStream = context.contentResolver.openOutputStream(uri)
                ?: return@runCatching false
            outputStream.use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(outputText)
                }
            }
            true
        }.getOrDefault(false)
    }

    suspend fun importNoteFromFile(uri: Uri): Note? = withContext(Dispatchers.IO) {
        runCatching {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: return@runCatching null
            val content = inputStream.use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    reader.readText()
                }
            }

            noteFileExporter.parseNoteFromText(content)
        }.getOrNull()
    }
}
