package com.example.notora.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.notora.data.local.entity.NoteEntity
import com.example.notora.data.local.entity.NoteLinkEntity
import com.example.notora.data.local.entity.NoteTagCrossRef
import com.example.notora.data.local.relation.NoteWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NoteDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE isArchived = 0 AND isTrashed = 0 ORDER BY isPinned DESC, updatedAt DESC")
    abstract fun getActiveNotes(): Flow<List<NoteWithDetails>>

    @Transaction
    @Query("SELECT * FROM notes WHERE isArchived = 1 AND isTrashed = 0 ORDER BY updatedAt DESC")
    abstract fun getArchivedNotes(): Flow<List<NoteWithDetails>>

    @Transaction
    @Query("SELECT * FROM notes WHERE isTrashed = 1 ORDER BY trashedAt DESC")
    abstract fun getTrashedNotes(): Flow<List<NoteWithDetails>>

    @Transaction
    @Query("SELECT * FROM notes WHERE isFavorite = 1 AND isTrashed = 0 ORDER BY updatedAt DESC")
    abstract fun getFavoriteNotes(): Flow<List<NoteWithDetails>>

    @Transaction
    @Query("SELECT * FROM notes WHERE isPinned = 1 AND isArchived = 0 AND isTrashed = 0 ORDER BY updatedAt DESC")
    abstract fun getPinnedNotes(): Flow<List<NoteWithDetails>>

    @Transaction
    @Query("SELECT * FROM notes WHERE categoryId = :categoryId AND isTrashed = 0 ORDER BY updatedAt DESC")
    abstract fun getNotesByCategory(categoryId: Long): Flow<List<NoteWithDetails>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    abstract fun getNoteById(noteId: Long): Flow<NoteWithDetails?>

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    abstract suspend fun getNoteByIdSync(noteId: Long): NoteWithDetails?

    @Transaction
    @Query("""
        SELECT * FROM notes
        WHERE (:includeTrashed = 1 OR isTrashed = 0)
        AND (:includeArchived = 1 OR isArchived = 0)
        AND id IN (
            SELECT rowid FROM notes_fts WHERE notes_fts MATCH :query || '*'
            UNION
            SELECT id FROM notes WHERE title LIKE '%' || :query || '%' OR plainTextContent LIKE '%' || :query || '%'
            UNION
            SELECT noteId FROM note_tag_cross_ref INNER JOIN tags ON note_tag_cross_ref.tagId = tags.id WHERE tags.name LIKE '%' || :query || '%'
        )
        AND (:categoryId IS NULL OR categoryId = :categoryId)
        AND (:tagId IS NULL OR id IN (SELECT noteId FROM note_tag_cross_ref WHERE tagId = :tagId))
        AND (:noteType IS NULL OR noteType = :noteType)
        AND (:colorHex IS NULL OR colorHex = :colorHex)
        AND (:isPinnedOnly = 0 OR isPinned = 1)
        AND (:isFavoriteOnly = 0 OR isFavorite = 1)
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    abstract fun searchNotesWithQuery(
        query: String,
        categoryId: Long? = null,
        tagId: Long? = null,
        noteType: String? = null,
        colorHex: String? = null,
        isPinnedOnly: Int = 0,
        isFavoriteOnly: Int = 0,
        includeArchived: Int = 0,
        includeTrashed: Int = 0
    ): Flow<List<NoteWithDetails>>

    @Transaction
    @Query("""
        SELECT * FROM notes
        WHERE (:includeTrashed = 1 OR isTrashed = 0)
        AND (:includeArchived = 1 OR isArchived = 0)
        AND (:categoryId IS NULL OR categoryId = :categoryId)
        AND (:tagId IS NULL OR id IN (SELECT noteId FROM note_tag_cross_ref WHERE tagId = :tagId))
        AND (:noteType IS NULL OR noteType = :noteType)
        AND (:colorHex IS NULL OR colorHex = :colorHex)
        AND (:isPinnedOnly = 0 OR isPinned = 1)
        AND (:isFavoriteOnly = 0 OR isFavorite = 1)
        ORDER BY isPinned DESC, updatedAt DESC
    """)
    abstract fun searchNotesWithoutQuery(
        categoryId: Long? = null,
        tagId: Long? = null,
        noteType: String? = null,
        colorHex: String? = null,
        isPinnedOnly: Int = 0,
        isFavoriteOnly: Int = 0,
        includeArchived: Int = 0,
        includeTrashed: Int = 0
    ): Flow<List<NoteWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertNote(note: NoteEntity): Long

    @Update
    abstract suspend fun updateNote(note: NoteEntity)

    @Delete
    abstract suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    abstract suspend fun deleteNoteById(id: Long)

    @Query("DELETE FROM notes WHERE isTrashed = 1")
    abstract suspend fun emptyTrash()

    // Tag Cross Reference Operations
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertNoteTagCrossRef(crossRef: NoteTagCrossRef)

    @Query("DELETE FROM note_tag_cross_ref WHERE noteId = :noteId")
    abstract suspend fun clearTagsForNote(noteId: Long)

    @Transaction
    open suspend fun setNoteTags(noteId: Long, tagIds: List<Long>) {
        clearTagsForNote(noteId)
        tagIds.forEach { tagId ->
            insertNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
        }
    }

    // Linked Notes / Backlinks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertNoteLink(link: NoteLinkEntity)

    @Query("DELETE FROM note_links WHERE sourceNoteId = :noteId OR targetNoteId = :noteId")
    abstract suspend fun clearNoteLinks(noteId: Long)

    @Query("SELECT targetNoteId FROM note_links WHERE sourceNoteId = :noteId")
    abstract suspend fun getLinkedTargetNoteIds(noteId: Long): List<Long>
}
