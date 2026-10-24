package com.example.notora

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.notora.domain.editor.RichSpanType
import com.example.notora.domain.editor.RichTextDocument
import com.example.notora.domain.editor.RichTextEditorState
import com.example.notora.domain.editor.RichTextSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RichTextEditorStateTest {

    @Test
    fun testToggleSpanStyleOnSelection() {
        val initialDoc = RichTextDocument("Hello World", emptyList())
        val state = RichTextEditorState(initialDoc)

        // Select "Hello"
        state.updateTextFieldValue(TextFieldValue("Hello World", selection = TextRange(0, 5)))
        state.toggleSpanStyle(RichSpanType.BOLD)

        assertTrue(state.isStyleActive(RichSpanType.BOLD))
        assertEquals(1, state.spans.size)
        assertEquals(0, state.spans[0].start)
        assertEquals(5, state.spans[0].end)
        assertEquals(RichSpanType.BOLD, state.spans[0].styleType)

        // Toggle bold off again
        state.toggleSpanStyle(RichSpanType.BOLD)
        assertTrue(state.spans.isEmpty())
    }

    @Test
    fun testUndoAndRedo() {
        val state = RichTextEditorState(RichTextDocument.fromPlainText("Initial"))

        state.updateTextFieldValue(TextFieldValue("Initial Text"))
        assertTrue(state.canUndo)

        state.undo()
        assertEquals("Initial", state.getDocument().text)
        assertTrue(state.canRedo)

        state.redo()
        assertEquals("Initial Text", state.getDocument().text)
    }
}
