package com.example.notora.core.utils

/**
 * Idiomatic String extension utilities across Notora.
 */
fun String.calculateWordCount(): Int {
    if (isBlank()) return 0
    return trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
}

fun String.calculateCharCount(includeSpaces: Boolean = true): Int {
    return if (includeSpaces) length else replace(Regex("\\s+"), "").length
}

fun Int.calculateReadingTimeMinutes(wordsPerMinute: Int = 200): Int {
    if (this <= 0) return 0
    return maxOf(1, (this + wordsPerMinute - 1) / wordsPerMinute)
}

fun String.cleanTagName(): String {
    return trim().removePrefix("#").trim()
}

fun String.truncateWithEllipsis(maxLength: Int): String {
    if (length <= maxLength) return this
    return take(maxLength).trimEnd() + "..."
}

fun String.extractFirstLine(maxLength: Int = 40, fallback: String = "Untitled Note"): String {
    val firstLine = lines().firstOrNull { it.isNotBlank() }?.trim() ?: return fallback
    return firstLine.truncateWithEllipsis(maxLength)
}

fun String?.cleanSearchQuery(): String {
    return this?.trim().orEmpty()
}
