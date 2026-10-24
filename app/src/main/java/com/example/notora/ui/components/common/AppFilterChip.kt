package com.example.notora.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.sdp

/**
 * Standardized FilterChip using AppDimensions, AppTypography and scalable sdp/ssp.
 */
@Composable
fun AppFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingBadge: @Composable (() -> Unit)? = null
) {
    FilterChip(
        selected = selected,
        onClick = rememberDebouncedClick { onClick() },
        label = {
            Text(
                text = label,
                style = AppTypography.labelMedium
            )
        },
        modifier = modifier.height(32.sdp),
        leadingIcon = leadingIcon,
        trailingIcon = trailingBadge,
        shape = RoundedCornerShape(AppDimensions.radiusFull),
        border = BorderStroke(
            1.sdp,
            if (selected) NotoraPrimary else NotoraPrimary.copy(alpha = 0.4f)
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = NotoraPrimary.copy(alpha = 0.15f),
            selectedLabelColor = NotoraPrimary,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
