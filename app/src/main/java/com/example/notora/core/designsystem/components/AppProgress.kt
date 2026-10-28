package com.example.notora.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.NotoraPrimary

/**
 * Standardized Circular Progress Indicator.
 */
@Composable
fun AppCircularProgress(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = NotoraPrimary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    strokeWidth: Dp = AppDimensions.borders.medium,
    strokeCap: StrokeCap = StrokeCap.Round,
    size: Dp = AppDimensions.iconLarge
) {
    val sizeModifier = modifier.size(size)
    if (progress != null) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = sizeModifier,
            color = color,
            trackColor = trackColor,
            strokeWidth = strokeWidth,
            strokeCap = strokeCap
        )
    } else {
        CircularProgressIndicator(
            modifier = sizeModifier,
            color = color,
            trackColor = trackColor,
            strokeWidth = strokeWidth,
            strokeCap = strokeCap
        )
    }
}

/**
 * Standardized Linear Progress Indicator.
 */
@Composable
fun AppLinearProgress(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = NotoraPrimary,
    trackColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
    strokeCap: StrokeCap = StrokeCap.Round,
    height: Dp = AppDimensions.paddingSmall
) {
    val progressModifier = modifier
        .fillMaxWidth()
        .height(height)
        .clip(RoundedCornerShape(AppDimensions.cornerMicro))

    if (progress != null) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = progressModifier,
            color = color,
            trackColor = trackColor,
            strokeCap = strokeCap
        )
    } else {
        LinearProgressIndicator(
            modifier = progressModifier,
            color = color,
            trackColor = trackColor,
            strokeCap = strokeCap
        )
    }
}
