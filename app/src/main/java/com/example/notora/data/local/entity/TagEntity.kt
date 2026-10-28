package com.example.notora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notora.domain.model.Tag

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

fun TagEntity.toDomainModel(): Tag = Tag(
    id = id,
    name = name
)

fun Tag.toEntity(): TagEntity = TagEntity(
    id = id,
    name = name
)
