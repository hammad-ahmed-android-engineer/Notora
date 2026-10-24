package com.example.notora.common.util

/**
 * Centralized string and text processing utilities across Notora.
 */
object StringUtils {

    /**
     * Accurately calculates word count from text.
     */
    fun calculateWordCount(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }

    /**
     * Calculates character count, optionally including whitespace.
     */
    fun calculateCharCount(text: String, includeSpaces: Boolean = true): Int {
        return if (includeSpaces) text.length else text.replace(Regex("\\s+"), "").length
    }

    /**
     * Calculates estimated reading time in minutes based on average 200 WPM.
     */
    fun calculateReadingTimeMinutes(wordCount: Int, wordsPerMinute: Int = 200): Int {
        if (wordCount <= 0) return 0
        return maxOf(1, (wordCount + wordsPerMinute - 1) / wordsPerMinute)
    }

    /**
     * Cleans tag name by removing leading '#' and extra whitespace.
     */
    fun cleanTagName(rawName: String): String {
        return rawName.trim().removePrefix("#").trim()
    }

    /**
     * Truncates text cleanly with ellipsis if it exceeds maxLength.
     */
    fun truncateWithEllipsis(text: String, maxLength: Int): String {
        if (text.length <= maxLength) return text
        return text.take(maxLength).trimEnd() + "..."
    }

    /**
     * Extracts the first non-blank line of text, truncating it cleanly if necessary.
     */
    fun extractFirstLine(text: String, maxLength: Int = 40, fallback: String = "Untitled Note"): String {
        val firstLine = text.lines().firstOrNull { it.isNotBlank() }?.trim() ?: return fallback
        return truncateWithEllipsis(firstLine, maxLength)
    }

    /**
     * Normalizes and cleans a search query.
     */
    fun cleanSearchQuery(query: String?): String {
        return query?.trim().orEmpty()
    }

    fun countWords(text: String): Int = calculateWordCount(text)

    fun countCharacters(text: String, includeSpaces: Boolean = true): Int =
        calculateCharCount(text, includeSpaces)

    fun truncate(text: String, maxLength: Int): String =
        truncateWithEllipsis(text, maxLength)

    /**
     * Checks if a string contains any non-whitespace content.
     */
    fun isNotBlank(text: String?): Boolean {
        return !text.isNullOrBlank()
    }
}
