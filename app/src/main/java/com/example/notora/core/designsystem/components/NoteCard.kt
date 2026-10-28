package com.example.notora.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NoteColorPalette
import com.example.notora.core.designsystem.NotoraAmber
import com.example.notora.core.designsystem.NotoraCyan
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.NotoraRose
import com.example.notora.core.utils.formatRelativeTime
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteType

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onPinClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onTrashClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTrashMode: Boolean = false,
    isArchiveMode: Boolean = false,
    onRestoreClick: (() -> Unit)? = null,
    onDeletePermanentlyClick: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    val backgroundColor = NoteColorPalette.getBackgroundColor(note.colorHex)

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = onClick,
        shape = RoundedCornerShape(AppDimensions.cornerLarge),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            AppDimensions.borders.thin,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(AppDimensions.paddingDefault),
        interactionSource = interactionSource
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Category, Note Type Icon, Pin, More Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.category != null) {
                        AppBadge(
                            text = note.category.name,
                            backgroundColor = NotoraPrimary,
                            contentColor = Color.White
                        )
                    } else {
                        AppBadge(
                            text = "Note",
                            icon = Icons.Default.Bookmark,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (note.noteType != NoteType.STANDARD) {
                        val iconVector = when (note.noteType) {
                            NoteType.CHECKLIST, NoteType.SHOPPING -> Icons.Default.CheckBox
                            else -> Icons.Default.Bookmark
                        }
                        AppBadge(
                            text = note.noteType.displayName,
                            icon = iconVector,
                            backgroundColor = NotoraCyan,
                            contentColor = Color.White
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isTrashMode) {
                        AppIconButton(
                            icon = Icons.Default.PushPin,
                            onClick = onPinClick,
                            contentDescription = stringResource(R.string.action_pin),
                            tint = if (note.isPinned) NotoraAmber else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            size = AppDimensions.iconLarge,
                            iconSize = AppDimensions.iconSmall
                        )
                    }

                    Box {
                        AppIconButton(
                            icon = Icons.Default.MoreVert,
                            onClick = { showMenu = true },
                            contentDescription = stringResource(R.string.action_more_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            size = AppDimensions.iconLarge,
                            iconSize = AppDimensions.iconSmall
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (isTrashMode) {
                                if (onRestoreClick != null) {
                                    DropdownMenuItem(
                                        text = { AppText(text = stringResource(R.string.action_restore)) },
                                        onClick = {
                                            showMenu = false
                                            onRestoreClick()
                                        },
                                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null) }
                                    )
                                }
                                if (onDeletePermanentlyClick != null) {
                                    DropdownMenuItem(
                                        text = { AppText(text = stringResource(R.string.action_delete), color = NotoraError) },
                                        onClick = {
                                            showMenu = false
                                            onDeletePermanentlyClick()
                                        },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = NotoraError) }
                                    )
                                }
                            } else {
                                DropdownMenuItem(
                                    text = { AppText(text = stringResource(if (note.isFavorite) R.string.notecard_unfavorite else R.string.notecard_favorite)) },
                                    onClick = {
                                        showMenu = false
                                        onFavoriteClick()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = null,
                                            tint = if (note.isFavorite) NotoraRose else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { AppText(text = stringResource(R.string.action_duplicate)) },
                                    onClick = {
                                        showMenu = false
                                        onDuplicateClick()
                                    }
                                )
                                if (isArchiveMode) {
                                    DropdownMenuItem(
                                        text = { AppText(text = stringResource(R.string.action_unarchive)) },
                                        onClick = {
                                            showMenu = false
                                            onArchiveClick()
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        text = { AppText(text = stringResource(R.string.action_archive)) },
                                        onClick = {
                                            showMenu = false
                                            onArchiveClick()
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { AppText(text = stringResource(R.string.action_delete), color = NotoraError) },
                                    onClick = {
                                        showMenu = false
                                        onTrashClick()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = NotoraError) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            // Title
            AppText(
                text = note.displayTitle,
                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (note.plainTextContent.isNotBlank()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                AppText(
                    text = note.previewText,
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Checklist Progress if checklist items exist
            if (note.checklistItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = "${note.checklistCompletedCount}/${note.checklistItems.size} tasks",
                        style = AppTypography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppText(
                        text = "${(note.checklistProgress * 100).toInt()}%",
                        style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NotoraPrimary
                    )
                }
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                AppLinearProgress(progress = note.checklistProgress)
            }

            // Footer Meta Info: Date, Word Count, Reminder
            Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = note.updatedAt.formatRelativeTime(),
                        style = AppTypography.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (note.wordCount > 0) {
                        AppText(
                            text = "• ${note.wordCount} words",
                            style = AppTypography.caption,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMicro),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.reminderTime != null && note.isReminderActive) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = NotoraPrimary,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                    }

                    if (note.attachments.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                    }
                }
            }

            // Tags row
            if (note.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMicro)
                ) {
                    note.tags.take(3).forEach { tag ->
                        AppBadge(
                            text = "#${tag.name}",
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            textStyle = AppTypography.caption
                        )
                    }
                    if (note.tags.size > 3) {
                        AppBadge(
                            text = "+${note.tags.size - 3}",
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            textStyle = AppTypography.caption
                        )
                    }
                }
            }
        }
    }
}
