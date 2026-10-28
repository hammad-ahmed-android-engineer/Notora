package com.example.notora.presentation.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraEmerald
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppBadge

/**
 * About section card displaying app branding, version metadata, and privacy statement.
 */
@Composable
fun SettingsAboutCard(
    modifier: Modifier = Modifier
) {
    SettingsGroupCard(modifier = modifier) {
        SettingsSectionHeader(
            title = stringResource(R.string.settings_section_about),
            icon = Icons.Default.Info,
            iconColor = NotoraEmerald
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = CircleShape,
                color = NotoraPrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(AppDimensions.components.fab)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_splash_logo),
                        contentDescription = stringResource(R.string.app_brand_title),
                        modifier = Modifier.size(AppDimensions.components.avatar)
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppDimensions.paddingLarge))

            Column {
                AppText(
                    text = stringResource(R.string.app_name),
                    style = AppTypography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                AppText(
                    text = stringResource(R.string.app_tagline),
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                AppText(
                    text = stringResource(R.string.settings_version),
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = NotoraPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

        AppBadge(
            text = "100% Offline & Private Knowledge Base",
            icon = Icons.Default.Security,
            backgroundColor = NotoraEmerald.copy(alpha = 0.12f),
            contentColor = NotoraEmerald,
            textStyle = AppTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            shape = RoundedCornerShape(AppDimensions.cornerMedium),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = AppDimensions.paddingDefault,
                vertical = AppDimensions.paddingMedium
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))

        AppText(
            text = stringResource(R.string.settings_about_desc),
            style = AppTypography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
