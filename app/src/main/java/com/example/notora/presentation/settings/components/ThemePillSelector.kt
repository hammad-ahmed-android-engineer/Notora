package com.example.notora.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.clickableDebounced

/**
 * Full-width theme selector pill row for switching between System, Light, and Dark themes.
 */
@Composable
fun ThemePillSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("SYSTEM", "LIGHT", "DARK")
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(AppDimensions.cornerLarge),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.paddingTiny),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMicro)
        ) {
            options.forEach { option ->
                val isSelected = selectedTheme == option
                val bgColor = if (isSelected) NotoraPrimary else Color.Transparent
                val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(AppDimensions.cornerMedium),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                        .clickableDebounced(debounceInterval = 500L) { onThemeSelected(option) }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = AppDimensions.paddingSmall),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (option) {
                                "SYSTEM" -> Icons.Default.PhoneAndroid
                                "LIGHT" -> Icons.Default.LightMode
                                else -> Icons.Default.DarkMode
                            },
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                        AppText(
                            text = when (option) {
                                "SYSTEM" -> "Auto"
                                "LIGHT" -> "Light"
                                else -> "Dark"
                            },
                            style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
