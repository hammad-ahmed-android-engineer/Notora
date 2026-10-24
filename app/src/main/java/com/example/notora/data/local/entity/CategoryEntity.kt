package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#4285F4",
    val iconName: String = "folder",
    val displayOrder: Int = 0,
    val isArchived: Boolean = false
)
