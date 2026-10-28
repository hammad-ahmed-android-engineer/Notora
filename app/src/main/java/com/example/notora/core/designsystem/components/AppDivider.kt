package com.example.notora.core.designsystem.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.notora.core.designsystem.AppDimensions

/**
 * Standardized Horizontal Divider.
 */
@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = AppDimensions.borders.thin,
    color: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}
