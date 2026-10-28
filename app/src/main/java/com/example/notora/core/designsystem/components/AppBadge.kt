package com.example.notora.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.rememberDebouncedClick

/**
 * Standardized AppBadge component for pill counts, word counts, dates, tags, and status chips.
 */
@Composable
fun AppBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    backgroundColor: Color = NotoraPrimary.copy(alpha = 0.12f),
    contentColor: Color = NotoraPrimary,
    textStyle: TextStyle = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
    shape: Shape = RoundedCornerShape(AppDimensions.cornerSmall),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AppDimensions.paddingMedium,
        vertical = AppDimensions.paddingSmall
    ),
    border: BorderStroke? = null,
    horizontalArrangement: androidx.compose.foundation.layout.Arrangement.Horizontal = androidx.compose.foundation.layout.Arrangement.Start,
    onClick: (() -> Unit)? = null
) {
    if (onClick != null) {
        val debouncedClick = rememberDebouncedClick { onClick() }
        Surface(
            onClick = debouncedClick,
            color = backgroundColor,
            contentColor = contentColor,
            shape = shape,
            border = border,
            modifier = modifier
        ) {
            BadgeContent(text, icon, contentColor, textStyle, contentPadding, horizontalArrangement)
        }
    } else {
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            shape = shape,
            border = border,
            modifier = modifier
        ) {
            BadgeContent(text, icon, contentColor, textStyle, contentPadding, horizontalArrangement)
        }
    }
}

@Composable
private fun BadgeContent(
    text: String,
    icon: ImageVector?,
    contentColor: Color,
    textStyle: TextStyle,
    contentPadding: PaddingValues,
    horizontalArrangement: androidx.compose.foundation.layout.Arrangement.Horizontal
) {
    Row(
        modifier = Modifier.padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(AppDimensions.iconSmall)
            )
            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
        }
        AppText(
            text = text,
            style = textStyle,
            color = contentColor
        )
    }
}
