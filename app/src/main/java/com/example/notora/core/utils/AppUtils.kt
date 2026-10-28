package com.example.notora.core.utils

import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteDateGroup
import com.example.notora.domain.model.NoteSortOption
import com.example.notora.domain.util.NoteDateGrouper
import java.util.Locale

/**
 * Backward-compatible facade delegating directly to Kotlin extension functions.
 */
object AppUtils {

    fun formatDate(timestamp: Long, locale: Locale = Locale.getDefault()): String = timestamp.formatDate(locale)
    fun formatDateTime(timestamp: Long, locale: Locale = Locale.getDefault()): String = timestamp.formatDateTime(locale)
    fun formatFullDate(timestamp: Long, locale: Locale = Locale.getDefault()): String = timestamp.formatFullDate(locale)
    fun formatDayKey(timestamp: Long, locale: Locale = Locale.getDefault()): String = timestamp.formatDayKey(locale)

    fun groupNotesByDate(
        notes: List<Note>,
        sortOption: NoteSortOption = NoteSortOption.MODIFIED_DESC
    ): List<NoteDateGroup> = NoteDateGrouper.groupNotes(notes, sortOption)

    fun calculateWordCount(text: String): Int = text.calculateWordCount()
    fun calculateCharCount(text: String, includeSpaces: Boolean = true): Int = text.calculateCharCount(includeSpaces)
    fun calculateReadingTimeMinutes(wordCount: Int, wordsPerMinute: Int = 200): Int = wordCount.calculateReadingTimeMinutes(wordsPerMinute)
    fun cleanTagName(rawName: String): String = rawName.cleanTagName()
    fun truncate(text: String, maxLength: Int): String = text.truncateWithEllipsis(maxLength)
    fun extractFirstLine(text: String, maxLength: Int = 40, fallback: String = "Untitled Note"): String = text.extractFirstLine(maxLength, fallback)
    fun cleanSearchQuery(query: String?): String = query.cleanSearchQuery()

    fun isValidTitle(title: String?): Boolean = title.isValidTitle()
    fun isValidTagName(name: String?): Boolean = name.isValidTagName()
    fun normalizeUrl(url: String): String = url.normalizeUrl()
    fun isValidHexColor(hex: String?): Boolean = hex.isValidHexColor()

    fun isLandscape(configuration: Configuration): Boolean = configuration.isLandscape()
    fun getGridColumnCount(configuration: Configuration, portraitColumns: Int = 2, landscapeColumns: Int = 3): Int = configuration.getGridColumnCount(portraitColumns, landscapeColumns)

    fun parseHexColor(hex: String?, fallback: Color = Color.Unspecified): Color = hex.parseHexColor(fallback)
}
