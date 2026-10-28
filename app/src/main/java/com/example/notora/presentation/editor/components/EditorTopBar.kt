package com.example.notora.presentation.editor.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraEmerald
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.NotoraRose
import com.example.notora.core.designsystem.components.AppIconButton
import com.example.notora.core.designsystem.components.AppText
import com.example.notora.core.designsystem.components.AppTopBar
import com.example.notora.core.utils.rememberDebouncedClick

/**
 * Top bar for the note editor screen providing status feedback and action buttons for
 * pinning, favoriting, palette selection, attachments, and deletion/history.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
    isPinned: Boolean,
    isFavorite: Boolean,
    noteId: Long,
    backgroundColor: Color,
    onBackClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onAttachFileClick: () -> Unit,
    onNavigateToRevisions: (Long) -> Unit,
    onDeleteNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
        label = "heart_scale"
    )

    AppTopBar(
        titleContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = NotoraEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(AppDimensions.cornerMedium)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingTiny),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = NotoraEmerald,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingMicro))
                        AppText(
                            text = stringResource(R.string.editor_saved_status),
                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NotoraEmerald,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        },
        onNavigateBack = onBackClick,
        actions = {
            AppIconButton(
                icon = Icons.Default.PushPin,
                onClick = onTogglePin,
                contentDescription = stringResource(R.string.action_pin),
                tint = if (isPinned) NotoraPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            AppIconButton(
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                onClick = onToggleFavorite,
                contentDescription = stringResource(R.string.action_favorite),
                tint = if (isFavorite) NotoraRose else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.scale(heartScale)
            )

            IconButton(
                onClick = rememberDebouncedClick { onOpenColorPicker() },
                modifier = Modifier.size(AppDimensions.components.avatar)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.iconLarge)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .border(AppDimensions.borders.medium, NotoraPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = stringResource(R.string.action_color),
                        modifier = Modifier.size(AppDimensions.iconSmall),
                        tint = NotoraPrimary
                    )
                }
            }

            AppIconButton(
                icon = Icons.Default.AttachFile,
                onClick = onAttachFileClick,
                contentDescription = stringResource(R.string.action_attach_file),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box {
                AppIconButton(
                    icon = Icons.Default.MoreVert,
                    onClick = { showOverflowMenu = true },
                    contentDescription = stringResource(R.string.action_more_options),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = { showOverflowMenu = false },
                    shape = RoundedCornerShape(AppDimensions.cornerLarge)
                ) {
                    if (noteId != 0L) {
                        DropdownMenuItem(
                            text = { AppText(stringResource(R.string.editor_menu_version_history), style = AppTypography.bodyMedium) },
                            onClick = rememberDebouncedClick {
                                showOverflowMenu = false
                                onNavigateToRevisions(noteId)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            AppText(
                                text = stringResource(R.string.editor_menu_delete),
                                style = AppTypography.bodyMedium,
                                color = NotoraError
                            )
                        },
                        onClick = rememberDebouncedClick {
                            showOverflowMenu = false
                            onDeleteNote()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = NotoraError
                            )
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    )
}
