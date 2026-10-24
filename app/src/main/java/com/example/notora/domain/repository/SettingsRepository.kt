package com.example.notora.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val layoutMode: Flow<String>
    val themeMode: Flow<String>
    val useDynamicColors: Flow<Boolean>
    val defaultNoteColor: Flow<String>
    val showWordCount: Flow<Boolean>

    suspend fun setLayoutMode(mode: String)
    suspend fun setThemeMode(theme: String)
    suspend fun setUseDynamicColors(enabled: Boolean)
    suspend fun setDefaultNoteColor(colorHex: String)
    suspend fun setShowWordCount(show: Boolean)
}
