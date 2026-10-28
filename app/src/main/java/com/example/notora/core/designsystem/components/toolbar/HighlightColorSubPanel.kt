package com.example.notora.core.designsystem.components.toolbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.utils.AppUtils
import com.example.notora.core.utils.clickableDebounced
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary

/**
 * Animated subpanel for selecting text highlight/background color.
 */
@Composable
fun HighlightColorSubPanel(
    visible: Boolean,
    currentHighlightHex: String?,
    onHighlightSelected: (String) -> Unit,
    onResetHighlight: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.paddingDefault, vertical = AppDimensions.paddingSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(
                    text = stringResource(R.string.richtext_highlight_title),
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HIGHLIGHT_COLORS.forEach { (hex, _) ->
                        val isSelected = hex.equals(currentHighlightHex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(AppDimensions.iconExtraLarge)
                                .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                                .background(AppUtils.parseHexColor(hex))
                                .border(
                                    width = if (isSelected) AppDimensions.borders.medium else AppDimensions.borders.thin,
                                    color = if (isSelected) NotoraPrimary else Color.Black.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(AppDimensions.cornerSmall)
                                )
                                .clickableDebounced(debounceInterval = 500L) { onHighlightSelected(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(AppDimensions.iconSmall)
                                )
                            }
                        }
                    }

                    // Clear highlight button
                    IconButton(
                        onClick = rememberDebouncedClick { onResetHighlight() },
                        modifier = Modifier.size(AppDimensions.iconExtraLarge)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatClear,
                            contentDescription = stringResource(R.string.richtext_remove_highlight_desc),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                    }
                }

                IconButton(
                    onClick = rememberDebouncedClick { onClose() },
                    modifier = Modifier.size(AppDimensions.iconLarge)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                }
            }
        }
    }
}
