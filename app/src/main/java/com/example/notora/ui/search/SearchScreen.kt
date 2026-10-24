package com.example.notora.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.components.FilterChipBar
import com.example.notora.ui.components.NoteCard
import com.example.notora.ui.components.common.AppEmptyState
import com.example.notora.ui.components.common.AppSearchField
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.sdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (noteId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val columns = AppUtils.getGridColumnCount(configuration, portraitColumns = 2, landscapeColumns = 3)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    AppSearchField(
                        query = uiState.filter.query,
                        onQueryChange = { viewModel.updateQuery(it) },
                        placeholder = stringResource(R.string.search_screen_placeholder),
                        onClear = { viewModel.clearQuery() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = rememberDebouncedClick { onNavigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
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
                filter = uiState.filter,
                categories = uiState.categories,
                tags = uiState.tags,
                onFilterChange = { viewModel.updateFilter(it) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.sdp, vertical = 6.sdp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = NotoraPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(AppDimensions.radiusSmall)
                ) {
                    Text(
                        text = "${uiState.resultCount} Matches",
                        style = AppTypography.badgeText,
                        color = NotoraPrimary,
                        modifier = Modifier.padding(horizontal = 10.sdp, vertical = 3.sdp)
                    )
                }
            }

            if (uiState.results.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Default.Search,
                    title = if (uiState.filter.query.isEmpty()) {
                        stringResource(R.string.search_empty_initial_title)
                    } else {
                        stringResource(R.string.search_empty_results_title)
                    },
                    description = stringResource(R.string.search_empty_results_desc),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(columns),
                    contentPadding = PaddingValues(AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(14.sdp),
                    verticalItemSpacing = 14.sdp,
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
