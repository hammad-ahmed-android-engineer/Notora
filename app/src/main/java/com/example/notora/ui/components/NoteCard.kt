package com.example.notora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.common.util.clickableDebounced
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteType
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NoteColorPalette
import com.example.notora.ui.theme.NotoraAmber
import com.example.notora.ui.theme.NotoraCyan
import com.example.notora.ui.theme.NotoraError
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.NotoraRose

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onPinClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onTrashClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val bgColor = NoteColorPalette.getBackgroundColor(note.colorHex)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth press physics animation
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    // Animated favorite bounce
    val heartScale by animateFloatAsState(
        targetValue = if (note.isFavorite) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "heart_scale"
    )

    // Dynamic subtle border
    val borderColor = when {
        note.isPinned -> NotoraPrimary.copy(alpha = 0.5f)
        note.isFavorite -> NotoraRose.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clip(RoundedCornerShape(20.dp))
            .clickableDebounced(
                debounceInterval = 500L,
                interactionSource = interactionSource,
                indication = null,
                onClick = rememberDebouncedClick { onClick() }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (note.isPinned) 4.dp else 2.dp,
            pressedElevation = 1.dp
        ),
        border = BorderStroke(if (note.isPinned) 1.2.dp else 0.8.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Category Pill / Type & Pin / Favorite / Overflow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.category != null) {
                    Surface(
                        color = NotoraPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = note.category.name,
                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NotoraPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            maxLines = 1
                        )
                    }
                } else {
                    Surface(
                        color = NotoraCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = note.noteType.displayName,
                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = NotoraCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = stringResource(R.string.home_filter_pinned),
                                tint = NotoraPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    if (note.isFavorite) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .scale(heartScale),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = stringResource(R.string.home_filter_favorites),
                                tint = NotoraRose,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Box {
                        IconButton(
                            onClick = rememberDebouncedClick { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.action_more_options),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            if (note.isTrashed) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_restore), style = AppTypography.bodyMedium, color = NotoraPrimary) },
                                    onClick = rememberDebouncedClick { showMenu = false; onClick() },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, tint = NotoraPrimary) }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.trash_action_delete_permanently), style = AppTypography.bodyMedium, color = NotoraError) },
                                    onClick = rememberDebouncedClick { showMenu = false; onTrashClick() },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = NotoraError) }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(if (note.isPinned) stringResource(R.string.notecard_unpin) else stringResource(R.string.notecard_pin), style = AppTypography.bodyMedium) },
                                    onClick = rememberDebouncedClick { showMenu = false; onPinClick() },
                                    leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null, tint = NotoraPrimary) }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (note.isFavorite) stringResource(R.string.notecard_unfavorite) else stringResource(R.string.notecard_favorite), style = AppTypography.bodyMedium) },
                                    onClick = rememberDebouncedClick { showMenu = false; onFavoriteClick() },
                                    leadingIcon = {
                                        Icon(
                                            if (note.isFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = NotoraRose
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_duplicate), style = AppTypography.bodyMedium) },
                                    onClick = rememberDebouncedClick { showMenu = false; onDuplicateClick() },
                                    leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (note.isArchived) stringResource(R.string.action_unarchive) else stringResource(R.string.action_archive), style = AppTypography.bodyMedium) },
                                    onClick = rememberDebouncedClick { showMenu = false; onArchiveClick() }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.editor_menu_delete), style = AppTypography.bodyMedium, color = NotoraError) },
                                    onClick = rememberDebouncedClick { showMenu = false; onTrashClick() },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = NotoraError) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            if (note.displayTitle.isNotBlank()) {
                Text(
                    text = note.displayTitle,
                    style = AppTypography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Preview Text
            if (note.previewText.isNotBlank()) {
                Text(
                    text = note.previewText,
                    style = AppTypography.bodyMedium.copy(
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Checklist Progress with sleek rounded indicator
            if (note.noteType == NoteType.CHECKLIST || note.checklistItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckBox,
                        contentDescription = stringResource(R.string.shortcut_new_checklist),
                        modifier = Modifier.size(16.dp),
                        tint = NotoraCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${note.checklistCompletedCount}/${note.checklistItems.size}",
                        style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    LinearProgressIndicator(
                        progress = { note.checklistProgress },
                        color = NotoraCyan,
                        trackColor = NotoraCyan.copy(alpha = 0.2f),
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Tag Chips with modern pill styling
            if (note.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    note.tags.take(3).forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "#${tag.name}",
                                style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Footer Badges & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.attachments.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = stringResource(R.string.editor_attachments_title),
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${note.attachments.size}",
                            style = AppTypography.labelSmall,
                            modifier = Modifier.padding(start = 2.dp, end = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (note.isReminderActive && note.reminderTime != null) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.notecard_reminder),
                            modifier = Modifier.size(14.dp),
                            tint = NotoraAmber
                        )
                    }
                }

                Text(
                    text = AppUtils.formatDate(note.updatedAt),
                    style = AppTypography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
