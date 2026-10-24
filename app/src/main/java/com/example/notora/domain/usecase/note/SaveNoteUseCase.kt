package com.example.notora.domain.usecase.note

import com.example.notora.common.util.AppUtils
import com.example.notora.domain.model.Note
import com.example.notora.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case to persist a note while guaranteeing stats calculation, timestamp updating,
 * valid color sanitization, and automatic revision snapshot generation.
 */
class SaveNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note, createRevisionSnapshot: Boolean = false): Long {
        val wordCount = AppUtils.calculateWordCount(note.plainTextContent)
        val charCount = AppUtils.calculateCharCount(note.plainTextContent)
        val readingTime = AppUtils.calculateReadingTimeMinutes(wordCount)
        val sanitizedColor = if (AppUtils.isValidHexColor(note.colorHex)) note.colorHex else "#FFFFFF"

        val noteToSave = note.copy(
            colorHex = sanitizedColor,
            wordCount = wordCount,
            charCount = charCount,
            readingTimeMinutes = readingTime,
            updatedAt = System.currentTimeMillis()
        )
        val savedId = noteRepository.saveNote(noteToSave)
        if (createRevisionSnapshot && savedId != 0L && (note.title.length > 5 || note.plainTextContent.length > 20)) {
            noteRepository.createRevision(savedId, note.title, note.contentJson)
        }
        return savedId
    }
}
