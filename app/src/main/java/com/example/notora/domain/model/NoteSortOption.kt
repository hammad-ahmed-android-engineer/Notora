package com.example.notora.domain.model

enum class NoteSortOption(val label: String) {
    MODIFIED_DESC("Last Modified (Newest)"),
    MODIFIED_ASC("Last Modified (Oldest)"),
    CREATED_DESC("Created Date (Newest)"),
    CREATED_ASC("Created Date (Oldest)"),
    TITLE_ASC("Title (A to Z)"),
    TITLE_DESC("Title (Z to A)"),
    COLOR("Color"),
    CATEGORY("Category")
}
