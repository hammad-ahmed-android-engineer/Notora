package com.example.notora.data.backup

import com.example.notora.common.util.AppUtils
import com.example.notora.domain.editor.RichTextDocument
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.model.Note
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supported file export formats for individual notes.
 */
enum class ExportFormat {
    TXT,
    MARKDOWN,
    HTML,
    JSON
}

/**
 * Handles serializing and deserializing notes across various document formats (TXT, MD, HTML, JSON).
 */
@Singleton
class NoteFileExporter @Inject constructor() {

    private val gson = Gson()

    /**
     * Converts a note and its rich text document into the specified string format.
     */
    fun formatNote(note: Note, format: ExportFormat): String {
        val document = RichTextSerializer.fromJson(note.contentJson)
        return when (format) {
            ExportFormat.TXT -> "${note.title}\n\n${document.text}"
            ExportFormat.MARKDOWN -> "# ${note.title}\n\n${RichTextSerializer.toMarkdown(document)}"
            ExportFormat.HTML -> "<h1>${note.title}</h1>\n${RichTextSerializer.toHtml(document)}"
            ExportFormat.JSON -> gson.toJson(note)
        }
    }

    /**
     * Parses raw text content into a note domain model.
     */
    fun parseNoteFromText(content: String): Note {
        val titleLine = content.lines().firstOrNull { it.isNotBlank() }?.removePrefix("# ")?.trim() ?: "Imported Note"
        val bodyText = content.lines().drop(1).joinToString("\n").trim()

        val doc = RichTextDocument.fromPlainText(bodyText.ifBlank { content })
        val wordCount = AppUtils.calculateWordCount(doc.text)
        val charCount = AppUtils.calculateCharCount(doc.text)

        return Note(
            id = 0,
            title = titleLine,
            contentJson = RichTextSerializer.toJson(doc),
            plainTextContent = doc.text,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            wordCount = wordCount,
            charCount = charCount,
            readingTimeMinutes = AppUtils.calculateReadingTimeMinutes(wordCount)
        )
    }
}
