package com.example.notora.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PATTERN_DATE = "MMM d, yyyy"
private const val PATTERN_DATE_TIME = "MMM d, yyyy - h:mm a"
private const val PATTERN_FULL_DATE = "EEEE, MMMM d, yyyy"
private const val PATTERN_DAY_KEY = "yyyyMMdd"

fun Long.formatDate(locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(PATTERN_DATE, locale).format(Date(this))
}

fun Long.formatDateTime(locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(PATTERN_DATE_TIME, locale).format(Date(this))
}

fun Long.formatFullDate(locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(PATTERN_FULL_DATE, locale).format(Date(this))
}

fun Long.formatDayKey(locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(PATTERN_DAY_KEY, locale).format(Date(this))
}

fun Long.formatRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val minute = 60 * 1000L
    val hour = 60 * minute
    val day = 24 * hour

    return when {
        diff < minute -> "Just now"
        diff < hour -> "${diff / minute}m ago"
        diff < day -> "${diff / hour}h ago"
        diff < 2 * day -> "Yesterday"
        diff < 7 * day -> "${diff / day}d ago"
        else -> formatDate()
    }
}
