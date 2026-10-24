package com.example.notora.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.clickableDebounced
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.domain.model.ChecklistItem
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraCyan
import com.example.notora.ui.theme.NotoraEmerald
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.NotoraError

@Composable
fun ChecklistComponent(
    items: List<ChecklistItem>,
    onItemChange: (List<ChecklistItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    var newItemText by remember { mutableStateOf("") }

    val completedCount = items.count { it.isCompleted }
    val progress = if (items.isEmpty()) 0f else completedCount.toFloat() / items.size
    val isAllDone = items.isNotEmpty() && completedCount == items.size

    Column(modifier = modifier.fillMaxWidth()) {
        // Progress Card
        if (items.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.checklist_progress_title),
                                style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (isAllDone) NotoraEmerald.copy(alpha = 0.2f) else NotoraPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isAllDone) stringResource(R.string.checklist_complete_celebration) else stringResource(R.string.checklist_completed_count, completedCount, items.size),
                                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isAllDone) NotoraEmerald else NotoraPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (completedCount > 0) {
                            TextButton(
                                onClick = rememberDebouncedClick {
                                    onItemChange(items.filter { !it.isCompleted })
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.checklist_clear_done),
                                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = NotoraError
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        color = if (isAllDone) NotoraEmerald else NotoraCyan,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                    )
                }
            }
        }

        // Add Item Row with rounded modern field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                placeholder = { Text(stringResource(R.string.checklist_add_item), style = AppTypography.inputHintText) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NotoraPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newItemText.isNotBlank()) {
                            val newItem = ChecklistItem(
                                id = 0,
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

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = rememberDebouncedClick {
                    if (newItemText.isNotBlank()) {
                        val newItem = ChecklistItem(
                            id = 0,
                            text = newItemText.trim(),
                            isCompleted = false,
                            displayOrder = items.size
                        )
                        onItemChange(items + newItem)
                        newItemText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NotoraPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.checklist_add_item_content_desc), tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Checklist Items List with spring animated checks
        items.forEachIndexed { index, item ->
            val itemAlpha by animateFloatAsState(
                targetValue = if (item.isCompleted) 0.55f else 1.0f,
                label = "item_alpha"
            )
            val checkScale by animateFloatAsState(
                targetValue = if (item.isCompleted) 1.1f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
                label = "check_scale"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (item.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Custom Animated Checkbox
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .scale(checkScale)
                        .clip(CircleShape)
                        .border(
                            width = 1.8.dp,
                            color = if (item.isCompleted) NotoraEmerald else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .background(
                            if (item.isCompleted) NotoraEmerald else Color.Transparent
                        )
                        .clickableDebounced(debounceInterval = 500L) {
                            val updated = items.toMutableList()
                            updated[index] = item.copy(isCompleted = !item.isCompleted)
                            onItemChange(updated)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.checklist_item_done_content_desc),
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Item Text Field
                BasicTextField(
                    value = item.text,
                    onValueChange = { newText ->
                        val updated = items.toMutableList()
                        updated[index] = item.copy(text = newText)
                        onItemChange(updated)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .alpha(itemAlpha),
                    singleLine = true,
                    textStyle = AppTypography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    cursorBrush = SolidColor(NotoraPrimary)
                )

                // Reorder controls
                if (index > 0) {
                    IconButton(
                        onClick = rememberDebouncedClick {
                            val updated = items.toMutableList()
                            val temp = updated[index]
                            updated[index] = updated[index - 1]
                            updated[index - 1] = temp
                            onItemChange(updated)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = stringResource(R.string.checklist_move_up), modifier = Modifier.size(15.dp))
                    }
                }

                if (index < items.size - 1) {
                    IconButton(
                        onClick = rememberDebouncedClick {
                            val updated = items.toMutableList()
                            val temp = updated[index]
                            updated[index] = updated[index + 1]
                            updated[index + 1] = temp
                            onItemChange(updated)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = stringResource(R.string.checklist_move_down), modifier = Modifier.size(15.dp))
                    }
                }

                IconButton(
                    onClick = rememberDebouncedClick {
                        val updated = items.toMutableList()
                        updated.removeAt(index)
                        onItemChange(updated)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.checklist_delete_item),
                        modifier = Modifier.size(15.dp),
                        tint = NotoraError.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
