package com.example.notora.domain.usecase.backup

import android.net.Uri
import com.example.notora.data.backup.BackupImportResult
import com.example.notora.data.backup.BackupRestoreManager
import javax.inject.Inject

/**
 * Use case to export application backup to JSON file.
 */
class ExportBackupUseCase @Inject constructor(
    private val backupRestoreManager: BackupRestoreManager
) {
    suspend operator fun invoke(uri: Uri): Boolean {
        return backupRestoreManager.exportBackupToJson(uri)
    }
}

/**
 * Use case to import application backup from JSON file.
 */
class ImportBackupUseCase @Inject constructor(
    private val backupRestoreManager: BackupRestoreManager
) {
    suspend operator fun invoke(uri: Uri): BackupImportResult {
        return backupRestoreManager.importBackupFromJson(uri)
    }
}
