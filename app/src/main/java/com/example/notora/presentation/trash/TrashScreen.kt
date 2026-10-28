package com.example.notora.presentation.trash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.utils.AppUtils
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.core.designsystem.components.NoteCard
import com.example.notora.core.designsystem.components.AppCircularProgress
import com.example.notora.core.designsystem.components.AppConfirmationDialog
import com.example.notora.core.designsystem.components.AppEmptyState
import com.example.notora.core.designsystem.components.AppIconButton
import com.example.notora.core.designsystem.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: TrashViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val columns = AppUtils.getGridColumnCount(configuration, portraitColumns = 2, landscapeColumns = 3)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.trash_screen_title),
                onNavigateBack = onNavigateBack,
                actions = {
                    if (uiState.trashedNotes.isNotEmpty()) {
                        AppIconButton(
                            icon = Icons.Default.DeleteSweep,
                            onClick = { showConfirmDialog = true },
                            contentDescription = stringResource(R.string.trash_action_empty_trash),
                            tint = NotoraError
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                AppCircularProgress()
            }
        } else if (uiState.trashedNotes.isEmpty()) {
            AppEmptyState(
                icon = Icons.Default.Delete,
                title = stringResource(R.string.trash_empty_title),
                description = uiState.errorMessage ?: stringResource(R.string.trash_empty_desc),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(columns),
                contentPadding = PaddingValues(AppDimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingDefault),
                verticalItemSpacing = AppDimensions.paddingDefault,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(uiState.trashedNotes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onClick = rememberDebouncedClick { viewModel.restoreNote(note.id) },
                        onPinClick = {},
                        onFavoriteClick = {},
                        onArchiveClick = {},
                        onTrashClick = rememberDebouncedClick { viewModel.permanentlyDelete(note.id) },
                        onDuplicateClick = {}
                    )
                }
            }
        }

        if (showConfirmDialog) {
            AppConfirmationDialog(
                title = stringResource(R.string.trash_dialog_empty_title),
                message = stringResource(R.string.trash_dialog_empty_message),
                confirmButtonText = stringResource(R.string.trash_action_empty_trash),
                isDestructive = true,
                icon = Icons.Default.DeleteSweep,
                onConfirm = {
                    viewModel.emptyTrash()
                    showConfirmDialog = false
                },
                onDismiss = { showConfirmDialog = false }
            )
        }
    }
}
