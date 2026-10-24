package com.example.notora.ui.components.toolbar

/**
 * Represents the currently visible secondary formatting panel in the rich text toolbar.
 */
enum class ActiveSubPanel {
    NONE, TEXT_COLOR, HIGHLIGHT, LINK
}

/**
 * Predefined curated palette for text ink colors.
 */
val TEXT_COLORS: List<Pair<String, String>> = listOf(
    Pair("#0F172A", "Ink"),
    Pair("#4F46E5", "Indigo"),
    Pair("#0284C7", "Sky"),
    Pair("#059669", "Emerald"),
    Pair("#D97706", "Amber"),
    Pair("#E11D48", "Rose"),
    Pair("#7C3AED", "Violet")
)

/**
 * Predefined curated palette for text highlighter marker colors.
 */
val HIGHLIGHT_COLORS: List<Pair<String, String>> = listOf(
    Pair("#FEF08A", "Lemon"),
    Pair("#BBF7D0", "Mint"),
    Pair("#BAE6FD", "Sky"),
    Pair("#FED7AA", "Peach"),
    Pair("#DDD6FE", "Lavender"),
    Pair("#FBCFE8", "Pink")
)
