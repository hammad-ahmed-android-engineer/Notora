package com.example.notora.data.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.notora.domain.model.Attachment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class AttachmentStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val attachmentsDir: File
        get() = File(context.filesDir, "attachments").apply { if (!exists()) mkdirs() }

    suspend fun saveAttachmentFromUri(uri: Uri): Attachment? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            val displayName = getFileName(uri) ?: "attachment_${System.currentTimeMillis()}"

            val destFile = File(attachmentsDir, "${UUID.randomUUID()}_$displayName")
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(destFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val savedUri = Uri.fromFile(destFile).toString()
            Attachment(
                id = 0,
                uri = savedUri,
                mimeType = mimeType,
                displayName = displayName,
                fileSizeBytes = destFile.length(),
                createdAt = System.currentTimeMillis()
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = cursor.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1 && result != null) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    suspend fun deleteAttachmentFile(fileUriString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = fileUriString.toUri()
            if (uri.scheme == "file") {
                val file = File(uri.path ?: return@withContext false)
                if (file.exists() && file.parentFile?.name == "attachments") {
                    return@withContext file.delete()
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
