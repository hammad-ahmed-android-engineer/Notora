package com.example.notora.data.repository

import com.example.notora.data.datastore.PreferencesDataSource
import com.example.notora.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : SettingsRepository {

    override val layoutMode: Flow<String> = preferencesDataSource.layoutMode
    override val themeMode: Flow<String> = preferencesDataSource.themeMode
    override val useDynamicColors: Flow<Boolean> = preferencesDataSource.useDynamicColors
    override val defaultNoteColor: Flow<String> = preferencesDataSource.defaultNoteColor
    override val showWordCount: Flow<Boolean> = preferencesDataSource.showWordCount

    override suspend fun setLayoutMode(mode: String) {
        preferencesDataSource.setLayoutMode(mode)
    }

    override suspend fun setThemeMode(theme: String) {
        preferencesDataSource.setThemeMode(theme)
    }

    override suspend fun setUseDynamicColors(enabled: Boolean) {
        preferencesDataSource.setUseDynamicColors(enabled)
    }

    override suspend fun setDefaultNoteColor(colorHex: String) {
        preferencesDataSource.setDefaultNoteColor(colorHex)
    }

    override suspend fun setShowWordCount(show: Boolean) {
        preferencesDataSource.setShowWordCount(show)
    }
}
