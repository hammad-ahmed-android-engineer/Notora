package com.example.notora.common.util

/**
 * Centralized validation utilities for user input, URLs, colors, and identifiers.
 * Pure Kotlin implementation for seamless performance and JVM unit-testability.
 */
object ValidationUtils {

    private val HEX_COLOR_REGEX = Regex("^#?([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")
    private val TAG_REGEX = Regex("^[a-zA-Z0-9_\\-]+$")
    private val URL_REGEX = Regex("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", RegexOption.IGNORE_CASE)

    /**
     * Validates whether a note or folder title is non-empty.
     */
    fun isValidTitle(title: String?): Boolean {
        return !title.isNullOrBlank() && title.trim().length <= 250
    }

    /**
     * Validates a tag name (1-50 alphanumeric, underscore, or dash characters).
     */
    fun isValidTagName(name: String?): Boolean {
        if (name == null) return false
        val clean = StringUtils.cleanTagName(name)
        return clean.isNotBlank() && clean.length <= 50 && TAG_REGEX.matches(clean)
    }

    /**
     * Validates a hex color code (3, 6, or 8 characters with optional leading #).
     */
    fun isValidHexColor(hex: String?): Boolean {
        return ColorUtils.isValidHexColor(hex)
    }

    /**
     * Validates whether a string is a valid web URL.
     */
    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val normalized = normalizeUrl(url)
        return URL_REGEX.matches(normalized)
    }

    /**
     * Normalizes a web URL by ensuring it contains an http:// or https:// prefix.
     */
    fun normalizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://", ignoreCase = true) &&
            !trimmed.startsWith("https://", ignoreCase = true)
        ) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }
}
