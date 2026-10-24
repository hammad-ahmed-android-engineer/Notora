package com.example.notora.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.NotoraError
import com.example.notora.ui.theme.sdp

/**
 * Standard confirmation dialog adhering to Notora's design system.
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
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isDestructive) NotoraError else NotoraPrimary
                )
            }
        },
        title = {
            Text(
                text = title,
                style = AppTypography.dialogTitle,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = AppTypography.dialogBody,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            AppTextButton(
                text = confirmButtonText,
                onClick = rememberDebouncedClick { onConfirm() },
                color = if (isDestructive) NotoraError else NotoraPrimary
            )
        },
        dismissButton = {
            AppTextButton(
                text = dismissButtonText,
                onClick = rememberDebouncedClick { onDismiss() },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(AppDimensions.radiusLarge)
    )
}

/**
 * Standard text input dialog (e.g. creating/editing category, adding tag).
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
    extraContent: (@Composable () -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = AppTypography.dialogTitle
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
                    Spacer(modifier = Modifier.height(8.sdp))
                    extraContent()
                }
            }
        },
        confirmButton = {
            AppTextButton(
                text = confirmButtonText,
                onClick = rememberDebouncedClick { onConfirm() },
                enabled = value.isNotBlank() && errorMessage.isNullOrBlank()
            )
        },
        dismissButton = {
            AppTextButton(
                text = dismissButtonText,
                onClick = rememberDebouncedClick { onDismiss() },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(AppDimensions.radiusLarge)
    )
}
