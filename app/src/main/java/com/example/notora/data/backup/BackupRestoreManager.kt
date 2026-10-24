package com.example.notora.data.backup

import android.content.Context
import android.net.Uri
import com.example.notora.data.local.dao.CategoryDao
import com.example.notora.data.local.dao.NoteDao
import com.example.notora.data.local.dao.TagDao
import com.example.notora.data.local.entity.CategoryEntity
import com.example.notora.data.local.entity.NoteEntity
import com.example.notora.data.local.entity.TagEntity
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.Tag
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
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
    private val gson = Gson()

    /**
     * Exports all active and archived notes, categories, and tags to a JSON file via [uri].
     */
    suspend fun exportBackupToJson(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val activeNotes = noteDao.getActiveNotes().first().map { it.toDomainModel() }
            val archivedNotes = noteDao.getArchivedNotes().first().map { it.toDomainModel() }
            val allNotes = activeNotes + archivedNotes

            val categories = categoryDao.getAllCategories().first().map {
                Category(
                    id = it.id,
                    name = it.name,
                    colorHex = it.colorHex,
                    iconName = it.iconName,
                    displayOrder = it.displayOrder,
                    isArchived = it.isArchived
                )
            }
            val tags = tagDao.getAllTags().first().map { Tag(id = it.id, name = it.name) }

            val backupData = NoteNestBackupData(
                version = 1,
                timestamp = System.currentTimeMillis(),
                notes = allNotes,
                categories = categories,
                tags = tags
            )

            val jsonString = gson.toJson(backupData)
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            outputStream?.use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(jsonString)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Imports categories, tags, and notes from a JSON backup file at [uri] after strict syntax and schema validation.
     */
    suspend fun importBackupFromJson(uri: Uri): BackupImportResult = withContext(Dispatchers.IO) {
        try {
            // 1. Verify file content & read stream
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val jsonString = inputStream?.use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    reader.readText()
                }
            }?.trim() ?: return@withContext BackupImportResult(false, errorMessage = "Could not read the selected file.")

            if (jsonString.isBlank()) {
                return@withContext BackupImportResult(false, errorMessage = "The selected file is empty.")
            }

            // 2. Strict JSON Syntax Validation using JsonParser
            val jsonElement = try {
                JsonParser.parseString(jsonString)
            } catch (_: Exception) {
                return@withContext BackupImportResult(
                    false,
                    errorMessage = "Invalid JSON file: Selected file is not a valid JSON document."
                )
            }

            if (!jsonElement.isJsonObject) {
                return@withContext BackupImportResult(
                    false,
                    errorMessage = "Invalid backup format: File must contain a valid Notora backup JSON object."
                )
            }

            val jsonObject = jsonElement.asJsonObject

            // 3. Schema Structure Validation
            val hasNotes = jsonObject.has("notes") && jsonObject.get("notes").isJsonArray
            val hasCategories = jsonObject.has("categories") && jsonObject.get("categories").isJsonArray
            val hasTags = jsonObject.has("tags") && jsonObject.get("tags").isJsonArray

            if (!hasNotes && !hasCategories && !hasTags) {
                return@withContext BackupImportResult(
                    false,
                    errorMessage = "Invalid schema: The JSON file does not match Notora's database backup schema."
                )
            }

            // 4. Gson Deserialization to BackupData
            val backupData = try {
                gson.fromJson(jsonString, NoteNestBackupData::class.java)
            } catch (_: Exception) {
                return@withContext BackupImportResult(
                    false,
                    errorMessage = "Schema mismatch: Unable to parse JSON fields into Notora database entities."
                )
            }

            if (backupData == null) {
                return@withContext BackupImportResult(
                    false,
                    errorMessage = "Invalid backup data: Unreadable backup structure."
                )
            }

            // 5. Entity Field Schema Validation
            val isNoteSchemaValid = backupData.notes.all { note ->
                note.title != null && note.contentJson != null && note.noteType != null
            }

            if (!isNoteSchemaValid) {
                return@withContext BackupImportResult(
                    false,
                    errorMessage = "Schema validation failed: Notes inside JSON contain incompatible entity fields."
                )
            }

            var importedCategories = 0
            var importedTags = 0
            var importedNotes = 0

            // Import categories
            backupData.categories.forEach { cat: Category ->
                val existing = categoryDao.getCategoryByName(cat.name)
                if (existing == null) {
                    categoryDao.insertCategory(
                        CategoryEntity(
                            name = cat.name,
                            colorHex = cat.colorHex,
                            iconName = cat.iconName,
                            displayOrder = cat.displayOrder,
                            isArchived = cat.isArchived
                        )
                    )
                    importedCategories++
                }
            }

            // Import tags
            backupData.tags.forEach { tag: Tag ->
                val existing = tagDao.getTagByName(tag.name)
                if (existing == null) {
                    tagDao.insertTag(TagEntity(name = tag.name))
                    importedTags++
                }
            }

            // Import notes
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

    /**
     * Formats and writes an individual note to the given [uri] in the specified [format].
     */
    suspend fun exportNoteToFile(note: Note, uri: Uri, format: ExportFormat): Boolean = withContext(Dispatchers.IO) {
        try {
            val outputText = noteFileExporter.formatNote(note, format)
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            outputStream?.use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(outputText)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Reads a note from the provided [uri] and parses it into a note domain entity.
     */
    suspend fun importNoteFromFile(uri: Uri): Note? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val content = inputStream?.use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    reader.readText()
                }
            } ?: return@withContext null

            noteFileExporter.parseNoteFromText(content)
        } catch (_: Exception) {
            null
        }
    }
}
