package com.example.notora.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#4285F4",
    val iconName: String = "folder",
    val displayOrder: Int = 0,
    val isArchived: Boolean = false,
    val noteCount: Int = 0
)
