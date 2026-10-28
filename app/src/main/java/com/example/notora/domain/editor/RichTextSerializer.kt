package com.example.notora.domain.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.notora.core.utils.AppUtils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RichTextSerializer {

    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    fun toJson(document: RichTextDocument): String {
        return jsonFormatter.encodeToString(document)
    }

    fun fromJson(jsonString: String?): RichTextDocument {
        if (jsonString.isNullOrBlank()) return RichTextDocument.empty()
        return try {
            val trimmed = jsonString.trim()
            if (trimmed.startsWith("{")) {
                jsonFormatter.decodeFromString<RichTextDocument>(trimmed)
            } else {
                RichTextDocument.fromPlainText(trimmed)
            }
        } catch (_: Exception) {
            RichTextDocument.fromPlainText(jsonString)
        }
    }

    fun toAnnotatedString(document: RichTextDocument): AnnotatedString {
        val rawText = document.text
        if (rawText.isEmpty()) return AnnotatedString("")

        return buildAnnotatedString {
            append(rawText)

            document.spans.forEach { span ->
                val start = span.start.coerceIn(0, rawText.length)
                val end = span.end.coerceIn(start, rawText.length)
                if (start >= end) return@forEach

                when (span.styleType) {
                    RichSpanType.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                    RichSpanType.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    RichSpanType.UNDERLINE -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                    RichSpanType.STRIKETHROUGH -> addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
                    RichSpanType.HEADING_1 -> addStyle(
                        SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold), start, end
                    )
                    RichSpanType.HEADING_2 -> addStyle(
                        SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold), start, end
                    )
                    RichSpanType.HEADING_3 -> addStyle(
                        SpanStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold), start, end
                    )
                    RichSpanType.INLINE_CODE -> addStyle(
                        SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x33808080)), start, end
                    )
                    RichSpanType.CODE_BLOCK -> addStyle(
                        SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0xFF1E293B), color = Color(0xFFF8FAFC)), start, end
                    )
                    RichSpanType.BLOCK_QUOTE -> addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic), start, end
                    )
                    RichSpanType.TEXT_COLOR -> {
                        span.value?.let { hex ->
                            parseColor(hex)?.let { color ->
                                addStyle(SpanStyle(color = color), start, end)
                            }
                        }
                    }
                    RichSpanType.HIGHLIGHT_COLOR -> {
                        span.value?.let { hex ->
                            parseColor(hex)?.let { color ->
                                addStyle(SpanStyle(background = color), start, end)
                            }
                        }
                    }
                    RichSpanType.LINK -> {
                        span.value?.let { url ->
                            addStyle(SpanStyle(color = Color(0xFF2563EB), textDecoration = TextDecoration.Underline), start, end)
                            addStringAnnotation(tag = "URL", annotation = url, start = start, end = end)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun parseColor(hex: String): Color? {
        val parsed = AppUtils.parseHexColor(hex)
        return if (parsed != Color.Unspecified) parsed else null
    }

    fun toMarkdown(document: RichTextDocument): String {
        val text = document.text
        if (text.isEmpty()) return ""

        var md = text
        document.spans.sortedByDescending { it.start }.forEach { span ->
            val start = span.start.coerceIn(0, md.length)
            val end = span.end.coerceIn(start, md.length)
            if (start >= end) return@forEach

            val snippet = md.substring(start, end)
            val formatted = when (span.styleType) {
                RichSpanType.BOLD -> "**$snippet**"
                RichSpanType.ITALIC -> "*$snippet*"
                RichSpanType.STRIKETHROUGH -> "~~$snippet~~"
                RichSpanType.INLINE_CODE -> "`$snippet`"
                RichSpanType.CODE_BLOCK -> "```\n$snippet\n```"
                RichSpanType.HEADING_1 -> "# $snippet"
                RichSpanType.HEADING_2 -> "## $snippet"
                RichSpanType.HEADING_3 -> "### $snippet"
                RichSpanType.BLOCK_QUOTE -> "> $snippet"
                RichSpanType.LINK -> "[$snippet](${span.value ?: ""})"
                else -> snippet
            }

            md = md.substring(0, start) + formatted + md.substring(end)
        }
        return md
    }

    fun toHtml(document: RichTextDocument): String {
        val text = document.text
        if (text.isEmpty()) return ""

        var html = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>")
        document.spans.sortedByDescending { it.start }.forEach { span ->
            val start = span.start.coerceIn(0, html.length)
            val end = span.end.coerceIn(start, html.length)
            if (start >= end) return@forEach

            val snippet = html.substring(start, end)
            val formatted = when (span.styleType) {
                RichSpanType.BOLD -> "<b>$snippet</b>"
                RichSpanType.ITALIC -> "<i>$snippet</i>"
                RichSpanType.UNDERLINE -> "<u>$snippet</u>"
                RichSpanType.STRIKETHROUGH -> "<s>$snippet</s>"
                RichSpanType.HEADING_1 -> "<h1>$snippet</h1>"
                RichSpanType.HEADING_2 -> "<h2>$snippet</h2>"
                RichSpanType.HEADING_3 -> "<h3>$snippet</h3>"
                RichSpanType.INLINE_CODE -> "<code>$snippet</code>"
                RichSpanType.CODE_BLOCK -> "<pre><code>$snippet</code></pre>"
                RichSpanType.BLOCK_QUOTE -> "<blockquote>$snippet</blockquote>"
                RichSpanType.LINK -> "<a href=\"${span.value ?: "#"}\">$snippet</a>"
                RichSpanType.TEXT_COLOR -> "<span style=\"color: ${span.value ?: "#000000"}\">$snippet</span>"
                RichSpanType.HIGHLIGHT_COLOR -> "<span style=\"background-color: ${span.value ?: "#FFFF00"}\">$snippet</span>"
                else -> snippet
            }

            html = html.substring(0, start) + formatted + html.substring(end)
        }
        return "<html><body>$html</body></html>"
    }

    fun fromMarkdown(markdown: String): RichTextDocument {
        val spans = mutableListOf<RichTextSpan>()
        var text = markdown

        // Parse Bold (**text**)
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        boldRegex.findAll(text).forEach { match ->
            val value = match.groupValues[1]
            val start = match.range.first
            spans.add(RichTextSpan(start, start + value.length, RichSpanType.BOLD))
        }
        text = text.replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")

        // Parse Italic (*text*)
        val italicRegex = Regex("\\*(.*?)\\*")
        italicRegex.findAll(text).forEach { match ->
            val value = match.groupValues[1]
            val start = match.range.first
            spans.add(RichTextSpan(start, start + value.length, RichSpanType.ITALIC))
        }
        text = text.replace(Regex("\\*(.*?)\\*"), "$1")

        return RichTextDocument(text, spans)
    }

    fun extractPlainText(document: RichTextDocument): String {
        return document.text.trim()
    }


}
