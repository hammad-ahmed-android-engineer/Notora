package com.example.notora.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppDivider
import com.example.notora.presentation.settings.SettingsUiState
import com.example.notora.presentation.settings.SettingsViewModel

/**
 * Appearance settings card providing theme mode and default home view preferences.
 */
@Composable
fun SettingsAppearanceCard(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    SettingsGroupCard(modifier = modifier) {
        SettingsSectionHeader(
            title = stringResource(R.string.settings_section_appearance),
            icon = Icons.Default.Palette,
            iconColor = NotoraPrimary
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            AppText(
                text = stringResource(R.string.settings_theme_mode),
                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
            AppText(
                text = "Select Light, Dark, or System Default theme",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
            ThemePillSelector(
                selectedTheme = uiState.themeMode,
                onThemeSelected = { viewModel.setThemeMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))
        AppDivider()
        Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

        Column(modifier = Modifier.fillMaxWidth()) {
            AppText(
                text = "Default Note View",
                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
            AppText(
                text = "Default layout style on Home screen",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
            LayoutPillSelector(
                selectedLayout = uiState.layoutMode,
                onLayoutSelected = { viewModel.setLayoutMode(it) }
            )
        }
    }
}
