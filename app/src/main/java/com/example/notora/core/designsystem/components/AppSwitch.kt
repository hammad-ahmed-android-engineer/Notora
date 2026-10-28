package com.example.notora.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.clickableDebounced

/**
 * Standardized AppSwitch wrapper adhering to Notora design tokens and debounced click guard.
 */
@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = AppTypography.bodyMedium,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    spacing: Dp = AppDimensions.paddingSmall,
    thumbContent: (@Composable () -> Unit)? = null,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
        checkedTrackColor = NotoraPrimary
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = if (onCheckedChange != null && enabled) {
            modifier.clickableDebounced { onCheckedChange(!checked) }
        } else {
            modifier
        }
    ) {
        if (!label.isNullOrBlank()) {
            AppText(
                text = label,
                style = textStyle,
                color = labelColor,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(spacing))
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            thumbContent = thumbContent,
            colors = colors,
            interactionSource = interactionSource
        )
    }
}
