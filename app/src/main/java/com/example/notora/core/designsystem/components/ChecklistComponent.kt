package com.example.notora.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.clickableDebounced
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.domain.model.ChecklistItem

/**
 * Interactive checklist component supporting item completion toggle, reordering, adding new items,
 * and live progress bar representation.
 */
@Composable
fun ChecklistComponent(
    items: List<ChecklistItem>,
    onItemChange: (List<ChecklistItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    var newItemText by remember { mutableStateOf("") }
    val totalCount = items.size
    val completedCount = items.count { it.isCompleted }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppDimensions.paddingSmall)
    ) {
        if (totalCount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.paddingDefault, vertical = AppDimensions.paddingMicro),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = stringResource(R.string.checklist_completed_count, completedCount, totalCount),
                    style = AppTypography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppText(
                    text = "${(progress * 100).toInt()}%",
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NotoraPrimary
                )
            }

            AppLinearProgress(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.paddingDefault),
                color = NotoraPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMicro)
        ) {
            items.forEachIndexed { index, item ->
                ChecklistItemRow(
                    item = item,
                    index = index,
                    totalCount = totalCount,
                    onItemUpdated = { updated ->
                        val updatedList = items.map { if (it.id == updated.id || (it.id == 0L && it == item)) updated else it }
                        onItemChange(updatedList)
                    },
                    onItemDeleted = { id ->
                        val updatedList = items.filter { it.id != id }
                        onItemChange(updatedList)
                    },
                    onItemMovedUp = { idx ->
                        if (idx > 0) {
                            val mutable = items.toMutableList()
                            val tmp = mutable[idx]
                            mutable[idx] = mutable[idx - 1]
                            mutable[idx - 1] = tmp
                            onItemChange(mutable)
                        }
                    },
                    onItemMovedDown = { idx ->
                        if (idx < items.size - 1) {
                            val mutable = items.toMutableList()
                            val tmp = mutable[idx]
                            mutable[idx] = mutable[idx + 1]
                            mutable[idx + 1] = tmp
                            onItemChange(mutable)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

        // Add New Checklist Item Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.paddingDefault),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                placeholder = stringResource(R.string.checklist_add_item),
                singleLine = true,
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = NotoraPrimary,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newItemText.isNotBlank()) {
                            val newItem = ChecklistItem(
                                id = System.currentTimeMillis(),
                                text = newItemText.trim(),
                                isCompleted = false,
                                displayOrder = items.size
                            )
                            onItemChange(items + newItem)
                            newItemText = ""
                        }
                    }
                )
            )

            if (newItemText.isNotBlank()) {
                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                AppTextButton(
                    text = stringResource(R.string.checklist_add_item_content_desc),
                    onClick = rememberDebouncedClick {
                        if (newItemText.isNotBlank()) {
                            val newItem = ChecklistItem(
                                id = System.currentTimeMillis(),
                                text = newItemText.trim(),
                                isCompleted = false,
                                displayOrder = items.size
                            )
                            onItemChange(items + newItem)
                            newItemText = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    index: Int,
    totalCount: Int,
    onItemUpdated: (ChecklistItem) -> Unit,
    onItemDeleted: (Long) -> Unit,
    onItemMovedUp: (Int) -> Unit,
    onItemMovedDown: (Int) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (item.isCompleted) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checklist_scale"
    )

    Surface(
        color = if (item.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        },
        shape = RoundedCornerShape(AppDimensions.cornerMedium),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.paddingDefault)
            .scale(scale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppDimensions.paddingDefault,
                    vertical = AppDimensions.paddingSmall
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickableDebounced(debounceInterval = 200L) {
                        onItemUpdated(item.copy(isCompleted = !item.isCompleted))
                    }
            ) {
                // Custom Checkbox Box
                Box(
                    modifier = Modifier
                        .size(AppDimensions.iconMedium)
                        .clip(CircleShape)
                        .background(
                            if (item.isCompleted) NotoraPrimary else Color.Transparent
                        )
                        .border(
                            width = AppDimensions.borders.medium,
                            color = if (item.isCompleted) NotoraPrimary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(AppDimensions.paddingDefault))

                var textState by remember(item.text) { mutableStateOf(item.text) }

                BasicTextField(
                    value = textState,
                    onValueChange = {
                        textState = it
                        onItemUpdated(item.copy(text = it))
                    },
                    textStyle = AppTypography.bodyMedium.copy(
                        color = if (item.isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
                    ),
                    cursorBrush = SolidColor(NotoraPrimary),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(if (item.isCompleted) 0.7f else 1.0f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (index > 0) {
                    AppIconButton(
                        icon = Icons.Default.ArrowUpward,
                        onClick = { onItemMovedUp(index) },
                        contentDescription = stringResource(R.string.checklist_move_up),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        size = AppDimensions.iconLarge,
                        iconSize = AppDimensions.iconSmall
                    )
                }

                if (index < totalCount - 1) {
                    AppIconButton(
                        icon = Icons.Default.ArrowDownward,
                        onClick = { onItemMovedDown(index) },
                        contentDescription = stringResource(R.string.checklist_move_down),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        size = AppDimensions.iconLarge,
                        iconSize = AppDimensions.iconSmall
                    )
                }

                AppIconButton(
                    icon = Icons.Default.Close,
                    onClick = { onItemDeleted(item.id) },
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    size = AppDimensions.iconLarge,
                    iconSize = AppDimensions.iconSmall
                )
            }
        }
    }
}
