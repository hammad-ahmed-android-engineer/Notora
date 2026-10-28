package com.example.notora.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.utils.rememberDebouncedClick

/**
 * Standardized AppIconButton with debounced click guard, customizable colors, size, and optional badges.
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = AppDimensions.components.avatar,
    iconSize: Dp = AppDimensions.iconMedium,
    badgeCount: Int? = null,
    showBadgeDot: Boolean = false,
    badgeColor: Color = NotoraError,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(contentColor = tint),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val debouncedClick = rememberDebouncedClick { onClick() }

    val iconContent = @Composable {
        IconButton(
            onClick = debouncedClick,
            modifier = modifier.size(size),
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) tint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.size(iconSize)
            )
        }
    }

    if (badgeCount != null && badgeCount > 0) {
        BadgedBox(
            badge = {
                Badge(containerColor = badgeColor) {
                    AppText(
                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                        style = AppTypography.badgeText,
                        color = Color.White
                    )
                }
            }
        ) {
            iconContent()
        }
    } else if (showBadgeDot) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = badgeColor,
                    modifier = Modifier.size(8.dp)
                )
            }
        ) {
            iconContent()
        }
    } else {
        iconContent()
    }
}
