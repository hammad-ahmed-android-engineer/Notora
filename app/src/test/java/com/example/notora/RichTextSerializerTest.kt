package com.example.notora

import com.example.notora.domain.editor.RichSpanType
import com.example.notora.domain.editor.RichTextDocument
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.editor.RichTextSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RichTextSerializerTest {

    @Test
    fun testJsonSerializationAndDeserialization() {
        val document = RichTextDocument(
            text = "Hello NoteNest",
            spans = listOf(
                RichTextSpan(0, 5, RichSpanType.BOLD),
                RichTextSpan(6, 14, RichSpanType.ITALIC)
            )
        )

        val json = RichTextSerializer.toJson(document)
        val deserialized = RichTextSerializer.fromJson(json)

        assertEquals(document.text, deserialized.text)
        assertEquals(document.spans.size, deserialized.spans.size)
        assertEquals(RichSpanType.BOLD, deserialized.spans[0].styleType)
        assertEquals(RichSpanType.ITALIC, deserialized.spans[1].styleType)
    }

    @Test
    fun testMarkdownExportAndImport() {
        val document = RichTextDocument(
            text = "Important Note",
            spans = listOf(
                RichTextSpan(0, 9, RichSpanType.BOLD)
            )
        )

        val md = RichTextSerializer.toMarkdown(document)
        assertEquals("**Important** Note", md)

        val importedDoc = RichTextSerializer.fromMarkdown("**Important** Note")
        assertEquals("Important Note", importedDoc.text)
        assertEquals(1, importedDoc.spans.size)
        assertEquals(RichSpanType.BOLD, importedDoc.spans[0].styleType)
    }

    @Test
    fun testHtmlExport() {
        val document = RichTextDocument(
            text = "Heading",
            spans = listOf(
                RichTextSpan(0, 7, RichSpanType.HEADING_1)
            )
        )

        val html = RichTextSerializer.toHtml(document)
        assertTrue(html.contains("<h1>Heading</h1>"))
    }

    @Test
    fun testWordAndCharCountCalculations() {
        val text = "The quick brown fox jumps over the lazy dog."

        val wordCount = RichTextSerializer.calculateWordCount(text)
        val charCountWithSpaces = RichTextSerializer.calculateCharCount(text, includeSpaces = true)
        val charCountWithoutSpaces = RichTextSerializer.calculateCharCount(text, includeSpaces = false)
        val readingTime = RichTextSerializer.calculateReadingTimeMinutes(wordCount)

        assertEquals(9, wordCount)
        assertEquals(44, charCountWithSpaces)
        assertEquals(36, charCountWithoutSpaces)
        assertEquals(1, readingTime)
    }
}
