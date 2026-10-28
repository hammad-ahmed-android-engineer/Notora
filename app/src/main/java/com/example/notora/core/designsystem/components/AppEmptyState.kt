package com.example.notora.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary

/**
 * Standardized empty state screen/card with icon, title, description, and optional action button.
 * Used consistently across Home, Categories, Tags, Trash, Archive, Calendar, and Search.
 */
@Composable
fun AppEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    titleStyle: TextStyle = AppTypography.headlineSmall,
    descriptionStyle: TextStyle = AppTypography.bodyMedium,
    iconTint: Color = NotoraPrimary.copy(alpha = 0.8f),
    iconContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    actionContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppDimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(AppDimensions.components.emptyStateIconContainer)
                .background(
                    color = iconContainerColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppDimensions.components.avatar),
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))

        AppText(
            text = title,
            style = titleStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

        AppText(
            text = description,
            style = descriptionStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        if (actionContent != null) {
            Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))
            actionContent()
        } else if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))
            AppButton(
                text = actionButtonText,
                onClick = onActionClick
            )
        }
    }
}
