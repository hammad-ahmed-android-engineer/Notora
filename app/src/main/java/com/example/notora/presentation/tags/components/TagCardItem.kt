package com.example.notora.presentation.tags.components

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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraCyan
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.domain.model.Tag
import com.example.notora.core.designsystem.components.AppCard
import com.example.notora.core.designsystem.components.AppIconButton

/**
 * Reusable card item displaying tag information with delete action.
 */
@Composable
fun TagCardItem(
    tag: Tag,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    color = NotoraCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(AppDimensions.cornerMedium),
                    modifier = Modifier.size(AppDimensions.components.searchBarHeight)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Label,
                            contentDescription = null,
                            tint = NotoraCyan,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(AppDimensions.paddingDefault))
                AppText(
                    text = "#${tag.name}",
                    style = AppTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            AppIconButton(
                icon = Icons.Default.Delete,
                onClick = onDeleteClick,
                contentDescription = stringResource(R.string.action_delete),
                tint = NotoraError.copy(alpha = 0.8f)
            )
        }
    }
}
