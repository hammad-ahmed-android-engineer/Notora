package com.example.notora.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.NotoraError
import com.example.notora.ui.theme.sdp

/**
 * Reusable, customizable input field adhering to Notora's centralized
 * design system, AppTypography, and scalable sdp/ssp dimensions.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = !errorMessage.isNullOrBlank(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 5,
    minLines: Int = 1,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    textStyle: TextStyle = AppTypography.inputFieldText,
    shape: Shape = RoundedCornerShape(AppDimensions.radiusMedium),
    maxLength: Int? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NotoraPrimary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        errorContainerColor = NotoraError.copy(alpha = 0.15f),
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )
) {
    Column(modifier = modifier) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                style = AppTypography.labelMedium,
                color = if (isError) NotoraError else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.sdp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (maxLength == null || newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = AppTypography.inputHintText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = shape,
            colors = colors,
            interactionSource = interactionSource
        )

        // Helper / Error / Character count row
        val showBottomRow = isError || !helperText.isNullOrBlank() || maxLength != null
        if (showBottomRow) {
            Spacer(modifier = Modifier.height(4.sdp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isError && !errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        style = AppTypography.caption,
                        color = NotoraError,
                        modifier = Modifier.weight(1f)
                    )
                } else if (!helperText.isNullOrBlank()) {
                    Text(
                        text = helperText,
                        style = AppTypography.helperText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (maxLength != null) {
                    Text(
                        text = "${value.length}/$maxLength",
                        style = AppTypography.caption,
                        color = if (value.length >= maxLength) {
                            NotoraError
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        }
                    )
                }
            }
        }
    }
}
