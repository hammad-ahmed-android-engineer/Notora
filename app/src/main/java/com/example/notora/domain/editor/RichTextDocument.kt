package com.example.notora.domain.editor

data class RichTextDocument(
    val text: String = "",
    val spans: List<RichTextSpan> = emptyList()
) {
    companion object {
        fun empty() = RichTextDocument("", emptyList())
        fun fromPlainText(plainText: String) = RichTextDocument(plainText, emptyList())
    }
}
