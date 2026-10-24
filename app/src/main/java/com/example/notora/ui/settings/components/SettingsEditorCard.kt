package com.example.notora.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notora.ui.settings.SettingsUiState
import com.example.notora.ui.settings.SettingsViewModel
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraCyan
import com.example.notora.ui.theme.NotoraPrimary

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
                Text(
                    text = "Word & Reading Counter",
                    style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Display live word metrics in note editor canvas",
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = uiState.showWordCount,
                onCheckedChange = { viewModel.setShowWordCount(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NotoraPrimary
                )
            )
        }
    }
}
