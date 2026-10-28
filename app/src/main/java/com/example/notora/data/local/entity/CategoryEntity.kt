package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notora.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#4285F4",
    val iconName: String = "folder",
    val displayOrder: Int = 0,
    val isArchived: Boolean = false
)

fun CategoryEntity.toDomainModel(): Category = Category(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName,
    displayOrder = displayOrder,
    isArchived = isArchived
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName,
    displayOrder = displayOrder,
    isArchived = isArchived
)
