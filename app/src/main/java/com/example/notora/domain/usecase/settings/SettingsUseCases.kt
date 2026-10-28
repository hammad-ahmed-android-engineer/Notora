package com.example.notora.domain.usecase.settings

import com.example.notora.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe or update theme mode setting.
 */
class GetThemeModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<String> = settingsRepository.themeMode
}

class SetThemeModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(theme: String) {
        settingsRepository.setThemeMode(theme)
    }
}

/**
 * Use case to observe or update note layout mode (grid / list).
 */
class GetLayoutModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<String> = settingsRepository.layoutMode
}

class SetLayoutModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(mode: String) {
        settingsRepository.setLayoutMode(mode)
    }
}

/**
 * Use case to observe or update show word count preference.
 */
class GetShowWordCountUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = settingsRepository.showWordCount
}

class SetShowWordCountUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(show: Boolean) {
        settingsRepository.setShowWordCount(show)
    }
}
