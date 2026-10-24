package com.example.notora.ui.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.notora.domain.editor.RichTextDocument
import com.example.notora.domain.editor.RichTextEditorState
import com.example.notora.domain.editor.RichTextSerializer

/**
 * VisualTransformation that renders rich text styling (bold, italic, headings,
 * inline code, block quotes, text colors, highlights, and links) live in BasicTextField.
 */
class RichTextVisualTransformation(
    private val editorState: RichTextEditorState
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val document = RichTextDocument(
            text = text.text,
            spans = editorState.spans.toList()
        )
        val annotated = RichTextSerializer.toAnnotatedString(document)
        return TransformedText(annotated, OffsetMapping.Identity)
    }
}
