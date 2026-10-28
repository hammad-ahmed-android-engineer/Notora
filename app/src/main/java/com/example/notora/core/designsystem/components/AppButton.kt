package com.example.notora.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.rememberDebouncedClick

/**
 * Standard primary button adhering to Notora's AppTypography, AppDimensions tokens, and 500ms debounced click guard.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(AppDimensions.cornerMedium),
    textStyle: TextStyle = AppTypography.buttonText,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    height: Dp? = AppDimensions.buttonHeight,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AppDimensions.paddingMedium,
        vertical = AppDimensions.paddingSmall
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val debouncedClick = rememberDebouncedClick { onClick() }
    val heightModifier = height?.let { modifier.height(it) } ?: modifier

    Button(
        onClick = debouncedClick,
        modifier = heightModifier,
        enabled = enabled && !isLoading,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(AppDimensions.iconMedium),
                strokeWidth = AppDimensions.borders.medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                }
                AppText(
                    text = text,
                    style = textStyle
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
            }
        }
    }
}

/**
 * Standard text button with full parameter options.
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = NotoraPrimary,
    shape: Shape = RoundedCornerShape(AppDimensions.cornerSmall),
    textStyle: TextStyle = AppTypography.buttonText,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val debouncedClick = rememberDebouncedClick { onClick() }

    TextButton(
        onClick = debouncedClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.textButtonColors(contentColor = color),
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        AppText(
            text = text,
            style = textStyle
        )
    }
}
