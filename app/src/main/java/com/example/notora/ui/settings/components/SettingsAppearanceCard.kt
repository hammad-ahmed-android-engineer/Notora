package com.example.notora.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.ui.settings.SettingsUiState
import com.example.notora.ui.settings.SettingsViewModel
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary

@Composable
fun SettingsSectionHeader(
    title: String,
    icon: ImageVector,
    iconColor: Color = NotoraPrimary,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(bottom = 12.dp)
    ) {
        Surface(
            color = iconColor.copy(alpha = 0.14f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = AppTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingsGroupCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            content()
        }
    }
}

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

        // Theme Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.settings_theme_mode),
                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Select Light, Dark, or System Default theme",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            ThemePillSelector(
                selectedTheme = uiState.themeMode,
                onThemeSelected = { viewModel.setThemeMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(16.dp))

        // Default Note View Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Default Note View",
                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Default layout style on Home screen",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            LayoutPillSelector(
                selectedLayout = uiState.layoutMode,
                onLayoutSelected = { viewModel.setLayoutMode(it) }
            )
        }
    }
}
