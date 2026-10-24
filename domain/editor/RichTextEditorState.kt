package com.example.notora.domain.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * High-performance state holder for rich text editing with optimized undo history allocations.
 */
class RichTextEditorState(
    initialDocument: RichTextDocument = RichTextDocument.empty()
) {
    var textFieldValue by mutableStateOf(TextFieldValue(initialDocument.text))
        private set

    val spans = mutableStateListOf<RichTextSpan>().apply {
        addAll(initialDocument.spans)
    }

    private val undoStack = ArrayDeque<RichTextDocument>()
    private val redoStack = ArrayDeque<RichTextDocument>()

    private var lastUndoTimestamp = 0L

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun updateTextFieldValue(newValue: TextFieldValue) {
        val oldText = textFieldValue.text
        val newText = newValue.text

        if (newText != oldText) {
            val now = System.currentTimeMillis()
            // Save undo snapshot on space/newline or if 1.5 seconds elapsed since last snapshot
            if (now - lastUndoTimestamp > 1500L || newText.endsWith(" ") || newText.endsWith("\n")) {
                saveToUndoHistory()
                lastUndoTimestamp = now
            }
            redoStack.clear()
            adjustSpanIndices(oldText, newText, newValue.selection)
        }
        textFieldValue = newValue
    }

    fun setDocument(document: RichTextDocument) {
        textFieldValue = TextFieldValue(document.text, TextRange(document.text.length))
        spans.clear()
        spans.addAll(document.spans)
        undoStack.clear()
        redoStack.clear()
    }

    fun getDocument(): RichTextDocument {
        return RichTextDocument(
            text = textFieldValue.text,
            spans = spans.toList()
        )
    }

    private fun saveToUndoHistory() {
        if (undoStack.size >= 30) undoStack.removeFirst()
        undoStack.addLast(getDocument())
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.addLast(getDocument())
            val previous = undoStack.removeLast()
            textFieldValue = TextFieldValue(previous.text, TextRange(previous.text.length))
            spans.clear()
            spans.addAll(previous.spans)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.addLast(getDocument())
            val next = redoStack.removeLast()
            textFieldValue = TextFieldValue(next.text, TextRange(next.text.length))
            spans.clear()
            spans.addAll(next.spans)
        }
    }

    fun toggleSpanStyle(type: RichSpanType, value: String? = null) {
        val selection = textFieldValue.selection
        val text = textFieldValue.text
        var start: Int
        var end: Int

        if (selection.collapsed) {
            val cursor = selection.start.coerceIn(0, text.length)
            val isBlockStyle = type in listOf(
                RichSpanType.HEADING_1, RichSpanType.HEADING_2, RichSpanType.HEADING_3,
                RichSpanType.BLOCK_QUOTE, RichSpanType.CODE_BLOCK
            )

            if (isBlockStyle) {
                // Expand to full line
                start = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
                end = text.indexOf('\n', cursor).let { if (it == -1) text.length else it }
            } else {
                // Expand to current word
                start = cursor
                while (start > 0 && !text[start - 1].isWhitespace()) start--
                end = cursor
                while (end < text.length && !text[end].isWhitespace()) end++
            }

            if (start >= end) {
                return
            }
        } else {
            start = selection.min
            end = selection.max
        }

        saveToUndoHistory()
        redoStack.clear()

        // If toggling a heading, clear other headings first
        if (type in listOf(RichSpanType.HEADING_1, RichSpanType.HEADING_2, RichSpanType.HEADING_3)) {
            spans.removeAll {
                it.overlaps(start, end) && it.styleType in listOf(
                    RichSpanType.HEADING_1, RichSpanType.HEADING_2, RichSpanType.HEADING_3
                )
            }
        }

        val existing = spans.filter { it.start == start && it.end == end && it.styleType == type }
        if (existing.isNotEmpty()) {
            spans.removeAll(existing)
        } else {
            spans.removeAll { it.overlaps(start, end) && it.styleType == type }
            spans.add(RichTextSpan(start, end, type, value))
        }
    }

    fun applyTextColor(hex: String?) {
        val selection = textFieldValue.selection
        val text = textFieldValue.text
        val start: Int
        val end: Int

        if (selection.collapsed) {
            val cursor = selection.start.coerceIn(0, text.length)
            var wStart = cursor
            while (wStart > 0 && !text[wStart - 1].isWhitespace()) wStart--
            var wEnd = cursor
            while (wEnd < text.length && !text[wEnd].isWhitespace()) wEnd++
            if (wStart >= wEnd) return
            start = wStart
            end = wEnd
        } else {
            start = selection.min
            end = selection.max
        }

        saveToUndoHistory()
        redoStack.clear()
        spans.removeAll { it.overlaps(start, end) && it.styleType == RichSpanType.TEXT_COLOR }

        if (!hex.isNullOrBlank()) {
            spans.add(RichTextSpan(start, end, RichSpanType.TEXT_COLOR, hex))
        }
    }

    fun applyHighlightColor(hex: String?) {
        val selection = textFieldValue.selection
        val text = textFieldValue.text
        val start: Int
        val end: Int

        if (selection.collapsed) {
            val cursor = selection.start.coerceIn(0, text.length)
            var wStart = cursor
            while (wStart > 0 && !text[wStart - 1].isWhitespace()) wStart--
            var wEnd = cursor
            while (wEnd < text.length && !text[wEnd].isWhitespace()) wEnd++
            if (wStart >= wEnd) return
            start = wStart
            end = wEnd
        } else {
            start = selection.min
            end = selection.max
        }

        saveToUndoHistory()
        redoStack.clear()
        spans.removeAll { it.overlaps(start, end) && it.styleType == RichSpanType.HIGHLIGHT_COLOR }

        if (!hex.isNullOrBlank()) {
            spans.add(RichTextSpan(start, end, RichSpanType.HIGHLIGHT_COLOR, hex))
        }
    }

    fun insertLink(url: String, displayText: String? = null) {
        val selection = textFieldValue.selection
        val text = textFieldValue.text
        saveToUndoHistory()
        redoStack.clear()

        if (selection.collapsed) {
            val label = if (!displayText.isNullOrBlank()) displayText else url
            val cursor = selection.start.coerceIn(0, text.length)
            val newText = text.substring(0, cursor) + label + text.substring(cursor)
            val linkSpan = RichTextSpan(cursor, cursor + label.length, RichSpanType.LINK, url)
            spans.add(linkSpan)
            textFieldValue = textFieldValue.copy(
                text = newText,
                selection = TextRange(cursor + label.length)
            )
        } else {
            val start = selection.min
            val end = selection.max
            spans.removeAll { it.overlaps(start, end) && it.styleType == RichSpanType.LINK }
            spans.add(RichTextSpan(start, end, RichSpanType.LINK, url))
        }
    }

    fun toggleBulletList() {
        val text = textFieldValue.text
        val cursor = textFieldValue.selection.start.coerceIn(0, text.length)
        val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val lineEnd = text.indexOf('\n', cursor).let { if (it == -1) text.length else it }
        val currentLine = text.substring(lineStart, lineEnd)

        saveToUndoHistory()
        redoStack.clear()

        if (currentLine.startsWith("• ")) {
            val newLine = currentLine.removePrefix("• ")
            val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
            adjustSpanIndices(text, newText, TextRange(lineStart))
            textFieldValue = textFieldValue.copy(
                text = newText,
                selection = TextRange((cursor - 2).coerceAtLeast(lineStart))
            )
        } else {
            val newLine = "• $currentLine"
            val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
            adjustSpanIndices(text, newText, TextRange(lineStart))
            textFieldValue = textFieldValue.copy(
                text = newText,
                selection = TextRange(cursor + 2)
            )
        }
    }

    fun toggleNumberedList() {
        val text = textFieldValue.text
        val cursor = textFieldValue.selection.start.coerceIn(0, text.length)
        val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
        val lineEnd = text.indexOf('\n', cursor).let { if (it == -1) text.length else it }
        val currentLine = text.substring(lineStart, lineEnd)

        saveToUndoHistory()
        redoStack.clear()

        val numRegex = Regex("^(\\d+)\\.\\s")
        val match = numRegex.find(currentLine)
        if (match != null) {
            val prefixLen = match.value.length
            val newLine = currentLine.substring(prefixLen)
            val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
            adjustSpanIndices(text, newText, TextRange(lineStart))
            textFieldValue = textFieldValue.copy(
                text = newText,
                selection = TextRange((cursor - prefixLen).coerceAtLeast(lineStart))
            )
        } else {
            val newLine = "1. $currentLine"
            val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
            adjustSpanIndices(text, newText, TextRange(lineStart))
            textFieldValue = textFieldValue.copy(
                text = newText,
                selection = TextRange(cursor + 3)
            )
        }
    }

    fun getActiveTextColor(): String? {
        val selection = textFieldValue.selection
        val cursor = selection.start.coerceIn(0, textFieldValue.text.length)
        return spans.firstOrNull { it.styleType == RichSpanType.TEXT_COLOR && cursor in it.start..it.end }?.value
    }

    fun getActiveHighlightColor(): String? {
        val selection = textFieldValue.selection
        val cursor = selection.start.coerceIn(0, textFieldValue.text.length)
        return spans.firstOrNull { it.styleType == RichSpanType.HIGHLIGHT_COLOR && cursor in it.start..it.end }?.value
    }

    fun getActiveHeading(): RichSpanType? {
        val selection = textFieldValue.selection
        val cursor = selection.start.coerceIn(0, textFieldValue.text.length)
        return spans.firstOrNull {
            cursor in it.start..it.end && it.styleType in listOf(
                RichSpanType.HEADING_1, RichSpanType.HEADING_2, RichSpanType.HEADING_3
            )
        }?.styleType
    }

    fun clearSelectionFormatting() {
        val selection = textFieldValue.selection
        val text = textFieldValue.text
        val start: Int
        val end: Int

        if (selection.collapsed) {
            val cursor = selection.start.coerceIn(0, text.length)
            var wStart = cursor
            while (wStart > 0 && !text[wStart - 1].isWhitespace()) wStart--
            var wEnd = cursor
            while (wEnd < text.length && !text[wEnd].isWhitespace()) wEnd++
            if (wStart >= wEnd) return
            start = wStart
            end = wEnd
        } else {
            start = selection.min
            end = selection.max
        }

        saveToUndoHistory()
        redoStack.clear()
        spans.removeAll { it.overlaps(start, end) }
    }

    fun isStyleActive(type: RichSpanType): Boolean {
        val selection = textFieldValue.selection
        if (selection.collapsed) {
            val cursor = selection.start.coerceIn(0, textFieldValue.text.length)
            return spans.any { it.styleType == type && cursor in it.start..it.end }
        } else {
            return spans.any { it.styleType == type && it.overlaps(selection.min, selection.max) }
        }
    }

    private fun adjustSpanIndices(oldText: String, newText: String, newSelection: TextRange) {
        val diff = newText.length - oldText.length
        if (diff == 0) return

        val cursor = newSelection.min
        val updated = spans.mapNotNull { span ->
            when {
                span.end <= cursor - maxOf(0, diff) -> span
                span.start >= cursor -> span.copy(start = span.start + diff, end = span.end + diff)
                else -> {
                    val newEnd = span.end + diff
                    if (newEnd > span.start) span.copy(end = newEnd) else null
                }
            }
        }
        spans.clear()
        spans.addAll(updated)
    }
}
