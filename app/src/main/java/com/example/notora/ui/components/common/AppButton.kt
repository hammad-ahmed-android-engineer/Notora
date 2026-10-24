package com.example.notora.ui.components.common

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.sdp

/**
 * Standard primary button adhering to Notora's AppTypography, sdp dimensions, and 500ms debounced click guard.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(AppDimensions.radiusMedium),
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    val debouncedClick = rememberDebouncedClick { onClick() }

    Button(
        onClick = debouncedClick,
        modifier = modifier.height(AppDimensions.buttonHeight),
        enabled = enabled && !isLoading,
        shape = shape,
        colors = colors,
        contentPadding = PaddingValues(
            horizontal = AppDimensions.paddingMedium,
            vertical = AppDimensions.paddingSmall
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.sdp),
                strokeWidth = 2.sdp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.sdp)
                    )
                    Spacer(modifier = Modifier.width(6.sdp))
                }
                Text(
                    text = text,
                    style = AppTypography.buttonText
                )
            }
        }
    }
}

/**
 * Standard outlined button.
 */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(AppDimensions.radiusMedium)
) {
    val debouncedClick = rememberDebouncedClick { onClick() }

    OutlinedButton(
        onClick = debouncedClick,
        modifier = modifier.height(AppDimensions.buttonHeight),
        enabled = enabled,
        shape = shape,
        border = BorderStroke(1.sdp, MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(
            horizontal = AppDimensions.paddingMedium,
            vertical = AppDimensions.paddingSmall
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.sdp)
                )
                Spacer(modifier = Modifier.width(6.sdp))
            }
            Text(
                text = text,
                style = AppTypography.buttonText
            )
        }
    }
}

/**
 * Standard tonal button.
 */
@Composable
fun AppTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(AppDimensions.radiusMedium)
) {
    val debouncedClick = rememberDebouncedClick { onClick() }

    FilledTonalButton(
        onClick = debouncedClick,
        modifier = modifier.height(AppDimensions.buttonHeight),
        enabled = enabled,
        shape = shape,
        contentPadding = PaddingValues(
            horizontal = AppDimensions.paddingMedium,
            vertical = AppDimensions.paddingSmall
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.sdp)
                )
                Spacer(modifier = Modifier.width(6.sdp))
            }
            Text(
                text = text,
                style = AppTypography.buttonText
            )
        }
    }
}

/**
 * Standard text button.
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = NotoraPrimary
) {
    val debouncedClick = rememberDebouncedClick { onClick() }

    TextButton(
        onClick = debouncedClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(contentColor = color)
    ) {
        Text(
            text = text,
            style = AppTypography.buttonText
        )
    }
}
