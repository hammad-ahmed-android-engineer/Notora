package com.example.notora.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppCircularProgress
import com.example.notora.core.designsystem.components.AppEmptyState
import com.example.notora.core.designsystem.components.AppText
import com.example.notora.core.designsystem.components.NoteCard
import com.example.notora.core.utils.AppUtils
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.domain.model.Note
import com.example.notora.presentation.home.HomeUiState

/**
 * Renders notes list either in staggered grid or single column list, with section headers for pinned
 * notes and temporal groupings.
 */
@Composable
fun HomeNotesGrid(
    uiState: HomeUiState,
    onNoteClick: (noteId: Long, noteType: String) -> Unit,
    onPinClick: (noteId: Long) -> Unit,
    onFavoriteClick: (noteId: Long) -> Unit,
    onArchiveClick: (noteId: Long) -> Unit,
    onTrashClick: (noteId: Long) -> Unit,
    onDuplicateClick: (Note) -> Unit,
    onCreateNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val columnCount = AppUtils.getGridColumnCount(configuration, portraitColumns = 2, landscapeColumns = 3)

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AppCircularProgress(color = NotoraPrimary)
        }
    } else if (uiState.pinnedNotes.isEmpty() && uiState.notes.isEmpty()) {
        AppEmptyState(
            icon = Icons.AutoMirrored.Filled.Notes,
            title = if (uiState.filter.query.isNotEmpty()) stringResource(R.string.home_empty_search_title) else stringResource(R.string.home_empty_title),
            description = if (uiState.filter.query.isNotEmpty()) stringResource(R.string.home_empty_search_desc) else stringResource(R.string.home_empty_desc),
            actionButtonText = stringResource(R.string.home_action_create_note),
            onActionClick = onCreateNoteClick,
            modifier = modifier
        )
    } else {
        if (uiState.layoutMode == "STAGGERED_GRID") {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(columnCount),
                contentPadding = PaddingValues(start = AppDimensions.paddingLarge, top = AppDimensions.paddingSmall, end = AppDimensions.paddingLarge, bottom = AppDimensions.components.emptyStateIconContainer),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingDefault),
                verticalItemSpacing = AppDimensions.paddingDefault,
                modifier = modifier.fillMaxSize()
            ) {
                if (uiState.pinnedNotes.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine, key = "pinned_section_header") {
                        PinnedSectionHeader(count = uiState.pinnedNotes.size)
                    }

                    items(
                        items = uiState.pinnedNotes,
                        key = { "pinned_${it.id}" },
                        span = {
                            if (uiState.pinnedNotes.size == 1) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
                        }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = rememberDebouncedClick { onNoteClick(note.id, note.noteType.name) },
                            onPinClick = rememberDebouncedClick { onPinClick(note.id) },
                            onFavoriteClick = rememberDebouncedClick { onFavoriteClick(note.id) },
                            onArchiveClick = rememberDebouncedClick { onArchiveClick(note.id) },
                            onTrashClick = rememberDebouncedClick { onTrashClick(note.id) },
                            onDuplicateClick = rememberDebouncedClick { onDuplicateClick(note) }
                        )
                    }
                }

                // Date-wise Grouped Notes
                uiState.dateGroups.forEach { dateGroup ->
                    item(span = StaggeredGridItemSpan.FullLine, key = "header_${dateGroup.id}") {
                        DateGroupHeader(
                            title = dateGroup.title,
                            count = dateGroup.notes.size
                        )
                    }

                    items(dateGroup.notes, key = { "note_${it.id}" }) { note ->
                        NoteCard(
                            note = note,
                            onClick = rememberDebouncedClick { onNoteClick(note.id, note.noteType.name) },
                            onPinClick = rememberDebouncedClick { onPinClick(note.id) },
                            onFavoriteClick = rememberDebouncedClick { onFavoriteClick(note.id) },
                            onArchiveClick = rememberDebouncedClick { onArchiveClick(note.id) },
                            onTrashClick = rememberDebouncedClick { onTrashClick(note.id) },
                            onDuplicateClick = rememberDebouncedClick { onDuplicateClick(note) }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = AppDimensions.paddingLarge, top = AppDimensions.paddingSmall, end = AppDimensions.paddingLarge, bottom = AppDimensions.components.emptyStateIconContainer),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingDefault),
                modifier = modifier.fillMaxSize()
            ) {
                if (uiState.pinnedNotes.isNotEmpty()) {
                    item(key = "pinned_list_header") {
                        PinnedSectionHeader(count = uiState.pinnedNotes.size)
                    }

                    items(uiState.pinnedNotes, key = { "pinned_list_${it.id}" }) { note ->
                        NoteCard(
                            note = note,
                            onClick = rememberDebouncedClick { onNoteClick(note.id, note.noteType.name) },
                            onPinClick = rememberDebouncedClick { onPinClick(note.id) },
                            onFavoriteClick = rememberDebouncedClick { onFavoriteClick(note.id) },
                            onArchiveClick = rememberDebouncedClick { onArchiveClick(note.id) },
                            onTrashClick = rememberDebouncedClick { onTrashClick(note.id) },
                            onDuplicateClick = rememberDebouncedClick { onDuplicateClick(note) }
                        )
                    }
                }

                // Date-wise Grouped Notes (List Layout)
                uiState.dateGroups.forEach { dateGroup ->
                    item(key = "header_list_${dateGroup.id}") {
                        DateGroupHeader(
                            title = dateGroup.title,
                            count = dateGroup.notes.size
                        )
                    }

                    items(dateGroup.notes, key = { "note_list_${it.id}" }) { note ->
                        NoteCard(
                            note = note,
                            onClick = rememberDebouncedClick { onNoteClick(note.id, note.noteType.name) },
                            onPinClick = rememberDebouncedClick { onPinClick(note.id) },
                            onFavoriteClick = rememberDebouncedClick { onFavoriteClick(note.id) },
                            onArchiveClick = rememberDebouncedClick { onArchiveClick(note.id) },
                            onTrashClick = rememberDebouncedClick { onTrashClick(note.id) },
                            onDuplicateClick = rememberDebouncedClick { onDuplicateClick(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PinnedSectionHeader(count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = AppDimensions.paddingSmall)
    ) {
        Icon(
            imageVector = Icons.Default.PushPin,
            contentDescription = null,
            tint = NotoraPrimary,
            modifier = Modifier.size(AppDimensions.iconSmall)
        )
        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
        AppText(
            text = stringResource(R.string.home_section_pinned),
            style = AppTypography.sectionHeader,
            color = NotoraPrimary
        )
        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
        Surface(
            color = NotoraPrimary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(AppDimensions.cornerSmall)
        ) {
            AppText(
                text = "$count",
                style = AppTypography.badgeText,
                color = NotoraPrimary,
                modifier = Modifier.padding(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingMicro)
            )
        }
    }
}

@Composable
private fun DateGroupHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = AppDimensions.paddingLarge, bottom = AppDimensions.paddingSmall)
    ) {
        AppText(
            text = title.uppercase(),
            style = AppTypography.sectionHeader,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            shape = RoundedCornerShape(AppDimensions.cornerSmall)
        ) {
            AppText(
                text = "$count",
                style = AppTypography.badgeText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingMicro)
            )
        }
    }
}
