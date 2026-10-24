package com.example.notora.ui.editor.components

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraEmerald
import com.example.notora.ui.theme.NotoraError
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.NotoraRose

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

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = NotoraEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = NotoraEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
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
        navigationIcon = {
            IconButton(
                onClick = rememberDebouncedClick { onBackClick() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = Color(0xFF0F172A)
                )
            }
        },
        actions = {
            IconButton(
                onClick = rememberDebouncedClick { onTogglePin() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = stringResource(R.string.action_pin),
                    tint = if (isPinned) NotoraPrimary else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = rememberDebouncedClick { onToggleFavorite() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.action_favorite),
                    tint = if (isFavorite) NotoraRose else Color(0xFF475569),
                    modifier = Modifier
                        .size(20.dp)
                        .scale(heartScale)
                )
            }

            IconButton(
                onClick = rememberDebouncedClick { onOpenColorPicker() },
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .border(1.5.dp, NotoraPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = stringResource(R.string.action_color),
                        modifier = Modifier.size(14.dp),
                        tint = NotoraPrimary
                    )
                }
            }

            IconButton(
                onClick = rememberDebouncedClick { onAttachFileClick() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = stringResource(R.string.action_attach_file),
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            }

            Box {
                IconButton(
                    onClick = rememberDebouncedClick { showOverflowMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.action_more_options),
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = { showOverflowMenu = false },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (noteId != 0L) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.editor_menu_version_history), style = AppTypography.bodyMedium) },
                            onClick = rememberDebouncedClick {
                                showOverflowMenu = false
                                onNavigateToRevisions(noteId)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
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
            navigationIconContentColor = Color(0xFF0F172A),
            actionIconContentColor = Color(0xFF475569)
        ),
        modifier = modifier
    )
}
