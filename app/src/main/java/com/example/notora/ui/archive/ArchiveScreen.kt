package com.example.notora.ui.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.ui.components.NoteCard
import com.example.notora.ui.components.common.AppEmptyState
import com.example.notora.ui.components.common.AppTopBar
import com.example.notora.ui.navigation.Screen
import com.example.notora.ui.navigation.components.AppBottomNavBar
import com.example.notora.ui.navigation.components.AppNavigationRail
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.sdp
import com.example.notora.common.util.rememberDebouncedClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    viewModel: ArchiveViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (noteId: Long) -> Unit,
    onNavigateToHome: () -> Unit = onNavigateBack,
    onNavigateToCategories: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val archivedNotes by viewModel.archivedNotes.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)
    val columns = AppUtils.getGridColumnCount(configuration, portraitColumns = 2, landscapeColumns = 3)

    val contentScaffold = @Composable { scaffoldModifier: Modifier ->
        Scaffold(
            modifier = scaffoldModifier,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.archive_screen_title),
                    onNavigateBack = onNavigateBack
                )
            },
            bottomBar = {
                if (!isLandscape) {
                    AppBottomNavBar(
                        currentRoute = Screen.ARCHIVE,
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToCategories = onNavigateToCategories,
                        onNavigateToCalendar = onNavigateToCalendar,
                        onNavigateToArchive = { /* Already here */ },
                        onNavigateToSettings = onNavigateToSettings,
                        onCreateNote = onNavigateToHome
                    )
                }
            }
        ) { paddingValues ->
            if (archivedNotes.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Default.Archive,
                    title = stringResource(R.string.archive_empty_title),
                    description = stringResource(R.string.archive_empty_desc),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(columns),
                    contentPadding = PaddingValues(AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(14.sdp),
                    verticalItemSpacing = 14.sdp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(
                        items = archivedNotes,
                        key = { it.id },
                        span = {
                            if (archivedNotes.size == 1) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
                        }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = rememberDebouncedClick { onNavigateToEditor(note.id) },
                            onPinClick = {},
                            onFavoriteClick = {},
                            onArchiveClick = rememberDebouncedClick { viewModel.unarchiveNote(note.id) },
                            onTrashClick = rememberDebouncedClick { viewModel.trashNote(note.id) },
                            onDuplicateClick = {}
                        )
                    }
                }
            }
        }
    }

    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize()) {
            AppNavigationRail(
                currentRoute = Screen.ARCHIVE,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToArchive = { /* Already here */ },
                onNavigateToSettings = onNavigateToSettings
            )
            contentScaffold(Modifier.weight(1f))
        }
    } else {
        contentScaffold(modifier.fillMaxSize())
    }
}
