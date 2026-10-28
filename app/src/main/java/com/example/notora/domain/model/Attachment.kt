package com.example.notora.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Long = 0,
    val noteId: Long = 0,
    val uri: String,
    val mimeType: String,
    val displayName: String,
    val fileSizeBytes: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
