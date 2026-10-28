package com.example.notora.core.utils

import androidx.compose.ui.graphics.Color

private val HEX_COLOR_REGEX = Regex("^#?([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")

fun String?.isValidHexColor(): Boolean {
    if (isNullOrBlank()) return false
    return HEX_COLOR_REGEX.matches(trim())
}

fun String?.parseHexColor(fallback: Color = Color.Unspecified): Color {
    if (isNullOrBlank()) return fallback
    val clean = trim().removePrefix("#")
    return try {
        when (clean.length) {
            3 -> {
                val r = clean.substring(0, 1).repeat(2).toInt(16)
                val g = clean.substring(1, 2).repeat(2).toInt(16)
                val b = clean.substring(2, 3).repeat(2).toInt(16)
                Color(r, g, b)
            }
            6 -> {
                val r = clean.substring(0, 2).toInt(16)
                val g = clean.substring(2, 4).toInt(16)
                val b = clean.substring(4, 6).toInt(16)
                Color(r, g, b)
            }
            8 -> {
                val a = clean.substring(0, 2).toInt(16)
                val r = clean.substring(2, 4).toInt(16)
                val g = clean.substring(4, 6).toInt(16)
                val b = clean.substring(6, 8).toInt(16)
                Color(r, g, b, a)
            }
            else -> fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

fun Color.toHexColor(): String {
    val r = (red * 255).toInt().coerceIn(0, 255)
    val g = (green * 255).toInt().coerceIn(0, 255)
    val b = (blue * 255).toInt().coerceIn(0, 255)
    return String.format("#%02X%02X%02X", r, g, b)
}
