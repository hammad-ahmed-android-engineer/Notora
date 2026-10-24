package com.example.notora.common.util

import androidx.compose.ui.graphics.Color

/**
 * Robust color conversion and validation utilities.
 * Handles hex strings safely without throwing runtime crashes.
 */
object ColorUtils {

    private val HEX_COLOR_REGEX = Regex("^#?([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")

    /**
     * Checks if a string is a valid 3, 6, or 8-digit hex color.
     */
    fun isValidHexColor(hex: String?): Boolean {
        if (hex.isNullOrBlank()) return false
        return HEX_COLOR_REGEX.matches(hex.trim())
    }

    /**
     * Safely parses a hex color string into a Compose [Color].
     * Returns [fallback] if parsing fails or input is invalid.
     */
    fun parseHexColor(hex: String?, fallback: Color = Color.Unspecified): Color {
        if (hex.isNullOrBlank()) return fallback
        val clean = hex.trim().removePrefix("#")
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

    /**
     * Formats a Compose [Color] into a standard 6-digit hex string (#RRGGBB).
     */
    fun toHexColor(color: Color): String {
        val red = (color.red * 255).toInt().coerceIn(0, 255)
        val green = (color.green * 255).toInt().coerceIn(0, 255)
        val blue = (color.blue * 255).toInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", red, green, blue)
    }
}
