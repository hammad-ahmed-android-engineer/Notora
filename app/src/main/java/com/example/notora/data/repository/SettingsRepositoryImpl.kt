package com.example.notora.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.notora.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val LAYOUT_MODE = stringPreferencesKey("layout_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val DEFAULT_NOTE_COLOR = stringPreferencesKey("default_note_color")
        val SHOW_WORD_COUNT = booleanPreferencesKey("show_word_count")
    }

    override val layoutMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAYOUT_MODE] ?: "STAGGERED_GRID"
    }

    override val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM"
    }

    override val useDynamicColors: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DYNAMIC_COLORS] ?: true
    }

    override val defaultNoteColor: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_NOTE_COLOR] ?: "#FFFFFF"
    }

    override val showWordCount: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHOW_WORD_COUNT] ?: true
    }

    override suspend fun setLayoutMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAYOUT_MODE] = mode
        }
    }

    override suspend fun setThemeMode(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = theme
        }
    }

    override suspend fun setUseDynamicColors(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLORS] = enabled
        }
    }

    override suspend fun setDefaultNoteColor(colorHex: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_NOTE_COLOR] = colorHex
        }
    }

    override suspend fun setShowWordCount(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_WORD_COUNT] = show
        }
    }
}
