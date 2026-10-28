package com.example.notora.presentation.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.AppUtils
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.core.designsystem.components.AppCircularProgress
import com.example.notora.core.designsystem.components.AppEmptyState
import com.example.notora.core.designsystem.components.AppInputDialog
import com.example.notora.core.designsystem.components.AppTopBar
import com.example.notora.presentation.tags.components.TagCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(
    viewModel: TagViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var tagName by rememberSaveable { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val columns = AppUtils.getGridColumnCount(configuration, portraitColumns = 1, landscapeColumns = 2)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.tags_screen_title),
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = rememberDebouncedClick {
                    tagName = ""
                    showDialog = true
                },
                containerColor = NotoraPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.tags_add_button),
                    modifier = Modifier.size(AppDimensions.cornerPill)
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                AppCircularProgress()
            }
        } else if (uiState.tags.isEmpty()) {
            AppEmptyState(
                icon = Icons.AutoMirrored.Filled.Label,
                title = stringResource(R.string.tags_empty_title),
                description = uiState.errorMessage ?: stringResource(R.string.tags_empty_desc),
                actionButtonText = stringResource(R.string.tags_add_button),
                onActionClick = rememberDebouncedClick {
                    tagName = ""
                    showDialog = true
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(AppDimensions.paddingLarge),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingLarge)
            ) {
                items(uiState.tags, key = { it.id }) { tag ->
                    TagCardItem(
                        tag = tag,
                        onDeleteClick = { viewModel.deleteTag(tag.id) }
                    )
                }
            }
        }

        if (showDialog) {
            val cleanName = AppUtils.cleanTagName(tagName)
            val isValid = AppUtils.isValidTagName(cleanName)
            AppInputDialog(
                title = stringResource(R.string.tags_dialog_new_title),
                value = tagName,
                onValueChange = { tagName = it },
                label = stringResource(R.string.tags_name_label),
                placeholder = stringResource(R.string.tags_name_placeholder),
                errorMessage = if (!isValid && tagName.isNotBlank()) stringResource(R.string.tags_error_invalid) else null,
                confirmButtonText = stringResource(R.string.tags_add_button),
                onConfirm = {
                    if (isValid) {
                        viewModel.createTag(cleanName)
                        showDialog = false
                    }
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}
