package com.example.notora.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class NoteType(val displayName: String, val iconName: String) {
    STANDARD("Standard Note", "description"),
    CHECKLIST("Checklist", "check_box"),
    SHOPPING("Shopping List", "shopping_cart"),
    MEETING("Meeting Note", "groups"),
    STUDY("Study Note", "school"),
    JOURNAL("Journal Entry", "book"),
    QUICK("Quick Note", "bolt"),
    DOCUMENT("Document", "article"),
    CODE("Code Snippet", "code"),
    IDEA("Idea", "lightbulb")
}
