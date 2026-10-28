package com.example.notora.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.NoteColorPalette
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.clickableDebounced

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ColorPickerBottomSheet(
    selectedColorHex: String,
    onColorSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val currentColor = NoteColorPalette.colors.firstOrNull { it.hex.equals(selectedColorHex, ignoreCase = true) }
        ?: NoteColorPalette.Default
    val backgroundColor = NoteColorPalette.getBackgroundColor(currentColor.hex)

    AppBottomSheet(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.color_picker_palette_title),
        headerAction = {
            AppBadge(
                text = currentColor.name,
                backgroundColor = backgroundColor.copy(alpha = 0.3f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
        ) {
            NoteColorPalette.colors.forEach { paletteColor ->
                val isSelected = paletteColor.hex.equals(selectedColorHex, ignoreCase = true)
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "color_scale"
                )
                val itemBg = NoteColorPalette.getBackgroundColor(paletteColor.hex)

                Box(
                    modifier = Modifier
                        .size(AppDimensions.components.colorPickerCircle)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(itemBg)
                        .border(
                            width = if (isSelected) AppDimensions.borders.thick else AppDimensions.borders.thin,
                            color = if (isSelected) NotoraPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickableDebounced(debounceInterval = 200L) {
                            onColorSelected(paletteColor.hex)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = NotoraPrimary,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.paddingExtraLarge))
    }
}
