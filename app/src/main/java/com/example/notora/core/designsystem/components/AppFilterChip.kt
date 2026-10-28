package com.example.notora.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.rememberDebouncedClick

/**
 * Standardized FilterChip using AppDimensions and AppTypography.
 */
@Composable
fun AppFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingBadge: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(AppDimensions.cornerPill),
    border: BorderStroke? = BorderStroke(
        AppDimensions.borders.thin,
        if (selected) NotoraPrimary else NotoraPrimary.copy(alpha = 0.4f)
    ),
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = NotoraPrimary.copy(alpha = 0.15f),
        selectedLabelColor = NotoraPrimary,
        containerColor = MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
) {
    FilterChip(
        selected = selected,
        onClick = rememberDebouncedClick { onClick() },
        label = {
            AppText(
                text = label,
                style = AppTypography.labelMedium
            )
        },
        modifier = modifier.height(AppDimensions.buttonSmallHeight),
        leadingIcon = leadingIcon,
        trailingIcon = trailingBadge,
        shape = shape,
        border = border,
        colors = colors
    )
}
