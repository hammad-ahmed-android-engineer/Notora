package com.example.notora.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.data.backup.BackupImportResult
import com.example.notora.data.backup.BackupRestoreManager
import com.example.notora.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: String = "SYSTEM",
    val layoutMode: String = "STAGGERED_GRID",
    val showWordCount: Boolean = true,
    val backupMessage: String? = null,
    val isBackupLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backupRestoreManager: BackupRestoreManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.layoutMode,
        settingsRepository.showWordCount
    ) { theme, layout, wordCount ->
        SettingsUiState(
            themeMode = theme,
            layoutMode = layout,
            showWordCount = wordCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(theme: String) {
        viewModelScope.launch { settingsRepository.setThemeMode(theme) }
    }

    fun setLayoutMode(layout: String) {
        viewModelScope.launch { settingsRepository.setLayoutMode(layout) }
    }

    fun setShowWordCount(show: Boolean) {
        viewModelScope.launch { settingsRepository.setShowWordCount(show) }
    }

    fun exportBackup(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = backupRestoreManager.exportBackupToJson(uri)
            onResult(success)
        }
    }

    fun importBackup(uri: Uri, onResult: (BackupImportResult) -> Unit) {
        viewModelScope.launch {
            val result = backupRestoreManager.importBackupFromJson(uri)
            onResult(result)
        }
    }
}
