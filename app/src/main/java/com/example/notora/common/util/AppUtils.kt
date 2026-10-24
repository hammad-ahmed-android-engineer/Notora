package com.example.notora.common.util

import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteDateGroup
import com.example.notora.domain.model.NoteSortOption
import com.example.notora.domain.util.NoteDateGrouper
import java.util.Locale

/**
 * Centralized AppUtils facade providing clean, unified access to all domain and UI utilities.
 *
 * Developers can either access modular namespaces (`AppUtils.string`, `AppUtils.dateTime`,
 * `AppUtils.validation`, `AppUtils.ui`, `AppUtils.color`) or use the top-level convenience methods.
 */
object AppUtils {

    val dateTime: DateTimeUtils = DateTimeUtils
    val string: StringUtils = StringUtils
    val validation: ValidationUtils = ValidationUtils
    val ui: UiUtils = UiUtils
    val color: ColorUtils = ColorUtils

    // --- Date & Time Conveniences ---
    fun formatDate(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatDate(timestamp, locale)

    fun formatDateTime(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatDateTime(timestamp, locale)

    fun formatFullDate(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatFullDate(timestamp, locale)

    fun formatMonthDayYear(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatMonthDayYear(timestamp, locale)

    fun formatDayKey(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatDayKey(timestamp, locale)

    fun formatRelativeTime(timestamp: Long): String =
        DateTimeUtils.formatRelativeTime(timestamp)

    fun groupNotesByDate(
        notes: List<Note>,
        sortOption: NoteSortOption = NoteSortOption.MODIFIED_DESC
    ): List<NoteDateGroup> =
        NoteDateGrouper.groupNotes(notes, sortOption)

    // --- String & Text Conveniences ---
    fun calculateWordCount(text: String): Int =
        StringUtils.calculateWordCount(text)

    fun calculateCharCount(text: String, includeSpaces: Boolean = true): Int =
        StringUtils.calculateCharCount(text, includeSpaces)

    fun calculateReadingTimeMinutes(wordCount: Int, wordsPerMinute: Int = 200): Int =
        StringUtils.calculateReadingTimeMinutes(wordCount, wordsPerMinute)

    fun cleanTagName(rawName: String): String =
        StringUtils.cleanTagName(rawName)

    fun truncate(text: String, maxLength: Int): String =
        StringUtils.truncateWithEllipsis(text, maxLength)

    fun extractFirstLine(text: String, maxLength: Int = 40, fallback: String = "Untitled Note"): String =
        StringUtils.extractFirstLine(text, maxLength, fallback)

    fun cleanSearchQuery(query: String?): String =
        StringUtils.cleanSearchQuery(query)

    // --- Validation Conveniences ---
    fun isValidTitle(title: String?): Boolean =
        ValidationUtils.isValidTitle(title)

    fun isValidTagName(name: String?): Boolean =
        ValidationUtils.isValidTagName(name)

    fun isValidUrl(url: String?): Boolean =
        ValidationUtils.isValidUrl(url)

    fun normalizeUrl(url: String): String =
        ValidationUtils.normalizeUrl(url)

    fun isValidHexColor(hex: String?): Boolean =
        ColorUtils.isValidHexColor(hex)

    // --- UI & Orientation Conveniences ---
    fun isLandscape(configuration: Configuration): Boolean =
        UiUtils.isLandscape(configuration)

    fun getGridColumnCount(configuration: Configuration, portraitColumns: Int = 2, landscapeColumns: Int = 3): Int =
        UiUtils.getGridColumnCount(configuration, portraitColumns, landscapeColumns)

    fun getGridColumnCount(isLandscape: Boolean, portraitColumns: Int = 2, landscapeColumns: Int = 3): Int =
        UiUtils.getGridColumnCount(isLandscape, portraitColumns, landscapeColumns)

    // --- Color Conveniences ---
    fun parseHexColor(hex: String?, fallback: Color = Color.Unspecified): Color =
        ColorUtils.parseHexColor(hex, fallback)

    fun toHexColor(color: Color): String =
        ColorUtils.toHexColor(color)
}
