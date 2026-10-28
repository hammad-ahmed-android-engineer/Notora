package com.example.notora.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.rememberDebouncedClick

/**
 * Standard confirmation dialog adhering to Notora's design system with full options.
 */
@Composable
fun AppConfirmationDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    isDestructive: Boolean = false,
    iconColor: Color? = null,
    titleStyle: TextStyle = AppTypography.dialogTitle,
    bodyStyle: TextStyle = AppTypography.dialogBody,
    confirmButtonColor: Color = if (isDestructive) NotoraError else NotoraPrimary,
    dismissButtonColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(AppDimensions.cornerLarge)
) {
    val tint = iconColor ?: if (isDestructive) NotoraError else NotoraPrimary

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = tint
                )
            }
        },
        title = {
            AppText(
                text = title,
                style = titleStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            AppText(
                text = message,
                style = bodyStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            AppTextButton(
                text = confirmButtonText,
                onClick = rememberDebouncedClick { onConfirm() },
                color = confirmButtonColor
            )
        },
        dismissButton = {
            AppTextButton(
                text = dismissButtonText,
                onClick = rememberDebouncedClick { onDismiss() },
                color = dismissButtonColor
            )
        },
        containerColor = containerColor,
        shape = shape
    )
}

/**
 * Standard text input dialog (e.g. creating/editing category, adding tag) with full options.
 */
@Composable
fun AppInputDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    label: String? = null,
    placeholder: String? = null,
    confirmButtonText: String = "Save",
    dismissButtonText: String = "Cancel",
    errorMessage: String? = null,
    titleStyle: TextStyle = AppTypography.dialogTitle,
    confirmButtonColor: Color = NotoraPrimary,
    dismissButtonColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(AppDimensions.cornerLarge),
    extraContent: (@Composable () -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AppText(
                text = title,
                style = titleStyle
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AppTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = label,
                    placeholder = placeholder,
                    errorMessage = errorMessage,
                    singleLine = true
                )
                if (extraContent != null) {
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    extraContent()
                }
            }
        },
        confirmButton = {
            AppTextButton(
                text = confirmButtonText,
                onClick = rememberDebouncedClick { onConfirm() },
                color = confirmButtonColor,
                enabled = value.isNotBlank() && errorMessage.isNullOrBlank()
            )
        },
        dismissButton = {
            AppTextButton(
                text = dismissButtonText,
                onClick = rememberDebouncedClick { onDismiss() },
                color = dismissButtonColor
            )
        },
        containerColor = containerColor,
        shape = shape
    )
}
