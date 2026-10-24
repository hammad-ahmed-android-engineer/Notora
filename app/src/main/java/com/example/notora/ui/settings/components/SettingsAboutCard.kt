package com.example.notora.ui.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notora.R
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraEmerald
import com.example.notora.ui.theme.NotoraPrimary

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
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_splash_logo),
                        contentDescription = stringResource(R.string.app_brand_title),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = AppTypography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    )
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.settings_version),
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = NotoraPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = NotoraEmerald.copy(alpha = 0.12f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = NotoraEmerald,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "100% Offline & Private Knowledge Base",
                    style = AppTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = NotoraEmerald
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.settings_about_desc),
            style = AppTypography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}
