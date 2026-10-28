package com.example.notora.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppBadge
import com.example.notora.core.designsystem.components.AppCircularProgress
import com.example.notora.core.designsystem.components.AppEmptyState
import com.example.notora.core.designsystem.components.AppSearchField
import com.example.notora.core.designsystem.components.AppTopBar
import com.example.notora.core.designsystem.components.FilterChipBar
import com.example.notora.core.designsystem.components.NoteCard
import com.example.notora.core.utils.AppUtils
import com.example.notora.core.utils.rememberDebouncedClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (noteId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val columns = AppUtils.getGridColumnCount(configuration, portraitColumns = 2, landscapeColumns = 3)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                titleContent = {
                    AppSearchField(
                        query = filter.query,
                        onQueryChange = { viewModel.updateQuery(it) },
                        placeholder = stringResource(R.string.search_screen_placeholder),
                        onClear = { viewModel.clearQuery() }
                    )
                },
                onNavigateBack = onNavigateBack,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterChipBar(
                filter = filter,
                categories = uiState.categories,
                tags = uiState.tags,
                onFilterChange = { viewModel.updateFilter(it) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.paddingLarge, vertical = AppDimensions.paddingSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBadge(
                    text = "${uiState.resultCount} Matches",
                    backgroundColor = NotoraPrimary.copy(alpha = 0.12f),
                    contentColor = NotoraPrimary,
                    textStyle = AppTypography.badgeText,
                    contentPadding = PaddingValues(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingTiny)
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AppCircularProgress()
                }
            } else if (uiState.results.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Default.Search,
                    title = if (filter.query.isEmpty()) {
                        stringResource(R.string.search_empty_initial_title)
                    } else {
                        stringResource(R.string.search_empty_results_title)
                    },
                    description = uiState.errorMessage ?: stringResource(R.string.search_empty_results_desc),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(columns),
                    contentPadding = PaddingValues(AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingDefault),
                    verticalItemSpacing = AppDimensions.paddingDefault,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.results, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = rememberDebouncedClick { onNavigateToEditor(note.id) },
                            onPinClick = rememberDebouncedClick { viewModel.togglePin(note.id) },
                            onFavoriteClick = rememberDebouncedClick { viewModel.toggleFavorite(note.id) },
                            onArchiveClick = rememberDebouncedClick { viewModel.archiveNote(note.id) },
                            onTrashClick = rememberDebouncedClick { viewModel.trashNote(note.id) },
                            onDuplicateClick = rememberDebouncedClick { viewModel.duplicateNote(note) }
                        )
                    }
                }
            }
        }
    }
}
