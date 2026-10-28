package com.example.notora.presentation.settings

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.data.backup.BackupImportResult
import com.example.notora.domain.usecase.backup.ExportBackupUseCase
import com.example.notora.domain.usecase.backup.ImportBackupUseCase
import com.example.notora.domain.usecase.settings.GetLayoutModeUseCase
import com.example.notora.domain.usecase.settings.GetShowWordCountUseCase
import com.example.notora.domain.usecase.settings.GetThemeModeUseCase
import com.example.notora.domain.usecase.settings.SetLayoutModeUseCase
import com.example.notora.domain.usecase.settings.SetShowWordCountUseCase
import com.example.notora.domain.usecase.settings.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SettingsUiState(
    val themeMode: String = "SYSTEM",
    val layoutMode: String = "STAGGERED_GRID",
    val showWordCount: Boolean = true,
    val backupMessage: String? = null,
    val isBackupLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    getLayoutModeUseCase: GetLayoutModeUseCase,
    private val setLayoutModeUseCase: SetLayoutModeUseCase,
    getShowWordCountUseCase: GetShowWordCountUseCase,
    private val setShowWordCountUseCase: SetShowWordCountUseCase,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        getThemeModeUseCase(),
        getLayoutModeUseCase(),
        getShowWordCountUseCase()
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
        viewModelScope.launch { setThemeModeUseCase(theme) }
    }

    fun setLayoutMode(layout: String) {
        viewModelScope.launch { setLayoutModeUseCase(layout) }
    }

    fun setShowWordCount(show: Boolean) {
        viewModelScope.launch { setShowWordCountUseCase(show) }
    }

    fun exportBackup(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = exportBackupUseCase(uri)
            onResult(success)
        }
    }

    fun importBackup(uri: Uri, onResult: (BackupImportResult) -> Unit) {
        viewModelScope.launch {
            val result = importBackupUseCase(uri)
            onResult(result)
        }
    }
}
