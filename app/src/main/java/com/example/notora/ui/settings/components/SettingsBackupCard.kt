package com.example.notora.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraCyan
import com.example.notora.ui.theme.NotoraPrimary

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

        Text(
            text = stringResource(R.string.settings_backup_desc),
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = rememberDebouncedClick { onExport() },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NotoraPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = NotoraPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.settings_backup_export),
                    style = AppTypography.buttonText,
                    color = NotoraPrimary
                )
            }

            ElevatedButton(
                onClick = rememberDebouncedClick { onImport() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = NotoraCyan,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.settings_backup_import),
                    style = AppTypography.buttonText
                )
            }
        }
    }
}
