package com.example.notora.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraCyan
import com.example.notora.core.designsystem.components.AppSwitch
import com.example.notora.presentation.settings.SettingsUiState
import com.example.notora.presentation.settings.SettingsViewModel

/**
 * Editor preferences settings card providing word count & reading time counter toggles.
 */
@Composable
fun SettingsEditorCard(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    SettingsGroupCard(modifier = modifier) {
        SettingsSectionHeader(
            title = "Editor Preferences",
            icon = Icons.Default.Tune,
            iconColor = NotoraCyan
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppText(
                    text = "Word & Reading Counter",
                    style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                AppText(
                    text = "Display live word metrics in note editor canvas",
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(AppDimensions.paddingMedium))

            AppSwitch(
                checked = uiState.showWordCount,
                onCheckedChange = { viewModel.setShowWordCount(it) }
            )
        }
    }
}
