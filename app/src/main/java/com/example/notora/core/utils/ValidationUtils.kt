package com.example.notora.core.utils

private val TAG_REGEX = Regex("^[a-zA-Z0-9_\\-]+$")

fun String?.isValidTitle(): Boolean {
    return !isNullOrBlank() && trim().length <= 250
}

fun String?.isValidTagName(): Boolean {
    if (this == null) return false
    val clean = cleanTagName()
    return clean.isNotBlank() && clean.length <= 50 && TAG_REGEX.matches(clean)
}

fun String.normalizeUrl(): String {
    val trimmed = trim()
    return if (!trimmed.startsWith("http://", ignoreCase = true) &&
        !trimmed.startsWith("https://", ignoreCase = true)
    ) {
        "https://$trimmed"
    } else {
        trimmed
    }
}
