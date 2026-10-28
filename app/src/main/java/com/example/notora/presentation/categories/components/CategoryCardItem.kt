package com.example.notora.presentation.categories.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.parseHexColor
import com.example.notora.domain.model.Category
import com.example.notora.core.designsystem.components.AppCard
import com.example.notora.core.designsystem.components.AppIconButton
import com.example.notora.core.designsystem.components.AppText

/**
 * Reusable card item displaying category information with edit and delete actions.
 */
@Composable
fun CategoryCardItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rawColor = category.colorHex.parseHexColor(fallback = NotoraPrimary)
    val luminance = (rawColor.red * 0.299f + rawColor.green * 0.587f + rawColor.blue * 0.114f)
    val catColor = if (luminance > 0.8f) NotoraPrimary else rawColor

    AppCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.cornerLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.paddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = catColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(AppDimensions.cornerMedium),
                    modifier = Modifier.size(AppDimensions.components.searchBarHeight)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = catColor,
                            modifier = Modifier.size(AppDimensions.iconLarge)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(AppDimensions.paddingDefault))
                AppText(
                    text = category.name,
                    style = AppTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row {
                AppIconButton(
                    icon = Icons.Default.Edit,
                    onClick = onEditClick,
                    contentDescription = stringResource(R.string.action_edit),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppIconButton(
                    icon = Icons.Default.Delete,
                    onClick = onDeleteClick,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = NotoraError.copy(alpha = 0.8f)
                )
            }
        }
    }
}
