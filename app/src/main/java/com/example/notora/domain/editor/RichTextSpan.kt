package com.example.notora.domain.editor

data class RichTextSpan(
    val start: Int,
    val end: Int,
    val styleType: RichSpanType,
    val value: String? = null
) {
    fun overlaps(selectionStart: Int, selectionEnd: Int): Boolean {
        if (start == end) return false
        val selStart = minOf(selectionStart, selectionEnd)
        val selEnd = maxOf(selectionStart, selectionEnd)
        return start < selEnd && end > selStart
    }
}
