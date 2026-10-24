package com.example.notora.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.notora.common.util.AppUtils
import com.example.notora.ui.components.FilterChipBar
import com.example.notora.ui.home.components.HomeDrawerContent
import com.example.notora.ui.home.components.HomeNotesGrid
import com.example.notora.ui.home.components.HomeSearchBar
import com.example.notora.ui.home.components.HomeSpeedDialFab
import com.example.notora.ui.navigation.Screen
import com.example.notora.ui.navigation.components.AppBottomNavBar
import com.example.notora.ui.navigation.components.AppNavigationRail
import kotlinx.coroutines.launch

/**
 * Main application screen presenting notes with filtering, multi-layout viewing (grid/list),
 * bottom navigation bar in portrait, navigation rail in landscape, and quick-action speed dial.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEditor: (noteId: Long?, noteType: String?) -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToTags: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)

    var isFabExpanded by rememberSaveable { mutableStateOf(false) }

    // Predictive Back Handling
    if (drawerState.isOpen) {
        BackHandler {
            scope.launch { drawerState.close() }
        }
    } else if (uiState.filter.query.isNotEmpty()) {
        BackHandler {
            viewModel.updateSearchQuery("")
        }
    }

    if (isLandscape) {
        // Landscape Layout with Navigation Rail
        Row(modifier = modifier.fillMaxSize()) {
            AppNavigationRail(
                currentRoute = Screen.HOME,
                onNavigateToHome = { /* Already on Home */ },
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToArchive = onNavigateToArchive,
                onNavigateToSettings = onNavigateToSettings
            )

            HomeMainScaffold(
                uiState = uiState,
                viewModel = viewModel,
                isLandscape = true,
                isFabExpanded = isFabExpanded,
                onToggleFab = { isFabExpanded = !isFabExpanded },
                onNavigateToEditor = onNavigateToEditor,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToArchive = onNavigateToArchive,
                onNavigateToSettings = onNavigateToSettings,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        // Portrait Layout with Navigation Drawer and Bottom Navigation Bar
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HomeDrawerContent(
                    totalNotesCount = uiState.notes.size + uiState.pinnedNotes.size,
                    categoriesCount = uiState.categories.size,
                    onNavigateToCategories = onNavigateToCategories,
                    onNavigateToTags = onNavigateToTags,
                    onNavigateToCalendar = onNavigateToCalendar,
                    onNavigateToTemplates = onNavigateToTemplates,
                    onNavigateToArchive = onNavigateToArchive,
                    onNavigateToTrash = onNavigateToTrash,
                    onNavigateToSettings = onNavigateToSettings,
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            HomeMainScaffold(
                uiState = uiState,
                viewModel = viewModel,
                isLandscape = false,
                isFabExpanded = isFabExpanded,
                onToggleFab = { isFabExpanded = !isFabExpanded },
                onNavigateToEditor = onNavigateToEditor,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onNavigateToCategories = onNavigateToCategories,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToArchive = onNavigateToArchive,
                onNavigateToSettings = onNavigateToSettings,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun HomeMainScaffold(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    isLandscape: Boolean,
    isFabExpanded: Boolean,
    onToggleFab: () -> Unit,
    onNavigateToEditor: (noteId: Long?, noteType: String?) -> Unit,
    onOpenDrawer: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeSearchBar(
                query = uiState.filter.query,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                isStaggeredGrid = uiState.layoutMode == "STAGGERED_GRID",
                onToggleLayout = { viewModel.toggleLayoutMode() },
                onOpenDrawer = onOpenDrawer,
                showDrawerButton = !isLandscape
            )
        },
        bottomBar = {
            if (!isLandscape) {
                AppBottomNavBar(
                    currentRoute = Screen.HOME,
                    onNavigateToHome = { /* Already on Home */ },
                    onNavigateToCategories = onNavigateToCategories,
                    onNavigateToCalendar = onNavigateToCalendar,
                    onNavigateToArchive = onNavigateToArchive,
                    onNavigateToSettings = onNavigateToSettings,
                    onCreateNote = { onNavigateToEditor(null, "STANDARD") }
                )
            }
        },
        floatingActionButton = {
            if (isLandscape) {
                HomeSpeedDialFab(
                    isExpanded = isFabExpanded,
                    onToggleExpand = onToggleFab,
                    onCreateStandardNote = { onNavigateToEditor(null, "STANDARD") },
                    onCreateChecklistNote = { onNavigateToEditor(null, "CHECKLIST") }
                )
            }
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

            HomeNotesGrid(
                uiState = uiState,
                onNoteClick = { noteId, noteType -> onNavigateToEditor(noteId, noteType) },
                onPinClick = { noteId -> viewModel.togglePin(noteId) },
                onFavoriteClick = { noteId -> viewModel.toggleFavorite(noteId) },
                onArchiveClick = { noteId -> viewModel.archiveNote(noteId) },
                onTrashClick = { noteId -> viewModel.trashNote(noteId) },
                onDuplicateClick = { note -> viewModel.duplicateNote(note) },
                onCreateNoteClick = { onNavigateToEditor(null, "STANDARD") }
            )
        }
    }
}
