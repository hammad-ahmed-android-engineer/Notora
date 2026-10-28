package com.example.notora.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraCyan
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppButton

/**
 * Backup and restore settings card providing JSON export and import options.
 */
@Composable
fun SettingsBackupCard(
    onExport: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsGroupCard(modifier = modifier) {
        SettingsSectionHeader(
            title = stringResource(R.string.settings_section_backup),
            icon = Icons.Default.Backup,
            iconColor = NotoraPrimary
        )

        AppText(
            text = stringResource(R.string.settings_backup_desc),
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
        ) {
            AppButton(
                text = stringResource(R.string.settings_backup_export),
                onClick = onExport,
                leadingIcon = Icons.Default.Download,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NotoraPrimary),
                modifier = Modifier.weight(1f)
            )

            AppButton(
                text = stringResource(R.string.settings_backup_import),
                onClick = onImport,
                leadingIcon = Icons.Default.Upload,
                colors = ButtonDefaults.buttonColors(containerColor = NotoraCyan, contentColor = Color.White),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
