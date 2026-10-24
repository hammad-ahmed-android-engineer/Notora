package com.example.notora.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Centralized date and time utilities across Notora.
 * Handles formatting, display strings, and relative timestamps consistently.
 */
object DateTimeUtils {

    private const val PATTERN_DATE = "MMM d, yyyy"
    private const val PATTERN_DATE_TIME = "MMM d, yyyy - h:mm a"
    private const val PATTERN_FULL_DATE = "EEEE, MMMM d, yyyy"
    private const val PATTERN_DAY_KEY = "yyyyMMdd"
    private const val PATTERN_MONTH_DAY_YEAR = "MMMM d, yyyy"

    /**
     * Formats timestamp to standard readable date (e.g. "Oct 24, 2026").
     */
    fun formatDate(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_DATE, locale).format(Date(timestamp))
    }

    /**
     * Formats timestamp to date and time (e.g. "Oct 24, 2026 - 1:45 PM").
     */
    fun formatDateTime(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_DATE_TIME, locale).format(Date(timestamp))
    }

    /**
     * Formats timestamp to executive full date (e.g. "Saturday, October 24, 2026").
     */
    fun formatFullDate(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_FULL_DATE, locale).format(Date(timestamp))
    }

    /**
     * Formats timestamp to month day year string (e.g. "October 24, 2026").
     */
    fun formatMonthDayYear(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_MONTH_DAY_YEAR, locale).format(Date(timestamp))
    }

    /**
     * Generates a comparable day key string (e.g. "20261024").
     */
    fun formatDayKey(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_DAY_KEY, locale).format(Date(timestamp))
    }

    /**
     * Formats timestamp to human-friendly relative time description.
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour

        return when {
            diff < minute -> "Just now"
            diff < hour -> "${diff / minute}m ago"
            diff < day -> "${diff / hour}h ago"
            diff < 2 * day -> "Yesterday"
            diff < 7 * day -> "${diff / day}d ago"
            else -> formatDate(timestamp)
        }
    }
}
