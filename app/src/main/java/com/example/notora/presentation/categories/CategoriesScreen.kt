package com.example.notora.presentation.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
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
import com.example.notora.presentation.categories.components.CategoryCardItem
import com.example.notora.core.designsystem.components.AppCircularProgress
import com.example.notora.core.designsystem.components.AppEmptyState
import com.example.notora.core.designsystem.components.AppInputDialog
import com.example.notora.core.designsystem.components.AppTopBar
import com.example.notora.presentation.navigation.Screen
import com.example.notora.presentation.navigation.components.AppBottomNavBar
import com.example.notora.presentation.navigation.components.AppNavigationRail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = onNavigateBack,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToArchive: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var editingCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var categoryName by rememberSaveable { mutableStateOf("") }
    val editingCategory = uiState.categories.find { it.id == editingCategoryId }

    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)
    val columns = AppUtils.getGridColumnCount(configuration, portraitColumns = 1, landscapeColumns = 2)

    val contentScaffold = @Composable { scaffoldModifier: Modifier ->
        Scaffold(
            modifier = scaffoldModifier,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.categories_screen_title),
                    onNavigateBack = onNavigateBack
                )
            },
            bottomBar = {
                if (!isLandscape) {
                    AppBottomNavBar(
                        currentRoute = Screen.CATEGORIES,
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToCategories = { /* Already here */ },
                        onNavigateToCalendar = onNavigateToCalendar,
                        onNavigateToArchive = onNavigateToArchive,
                        onNavigateToSettings = onNavigateToSettings,
                        onCreateNote = {
                            editingCategoryId = null
                            categoryName = ""
                            showDialog = true
                        }
                    )
                }
            },
            floatingActionButton = {
                if (isLandscape) {
                    FloatingActionButton(
                        onClick = rememberDebouncedClick {
                            editingCategoryId = null
                            categoryName = ""
                            showDialog = true
                        },
                        containerColor = NotoraPrimary,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.categories_add_button),
                            modifier = Modifier.size(AppDimensions.cornerPill)
                        )
                    }
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
            } else if (uiState.categories.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Default.Folder,
                    title = stringResource(R.string.categories_empty_title),
                    description = uiState.errorMessage ?: stringResource(R.string.categories_empty_desc),
                    actionButtonText = stringResource(R.string.categories_add_button),
                    onActionClick = rememberDebouncedClick {
                        editingCategoryId = null
                        categoryName = ""
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
                    items(uiState.categories, key = { it.id }) { category ->
                        CategoryCardItem(
                            category = category,
                            onEditClick = {
                                editingCategoryId = category.id
                                categoryName = category.name
                                showDialog = true
                            },
                            onDeleteClick = {
                                viewModel.deleteCategory(category.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize()) {
            AppNavigationRail(
                currentRoute = Screen.CATEGORIES,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCategories = { /* Already here */ },
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToArchive = onNavigateToArchive,
                onNavigateToSettings = onNavigateToSettings
            )
            contentScaffold(Modifier.weight(1f))
        }
    } else {
        contentScaffold(modifier.fillMaxSize())
    }

    if (showDialog) {
        AppInputDialog(
            title = if (editingCategory != null) stringResource(R.string.categories_dialog_edit_title) else stringResource(R.string.categories_dialog_new_title),
            value = categoryName,
            onValueChange = { categoryName = it },
            placeholder = stringResource(R.string.categories_name_placeholder),
            confirmButtonText = if (editingCategory != null) stringResource(R.string.action_save) else stringResource(R.string.categories_add_button),
            onConfirm = {
                if (categoryName.isNotBlank()) {
                    if (editingCategory != null) {
                        viewModel.saveCategory(editingCategory.id, categoryName.trim(), editingCategory.colorHex)
                    } else {
                        viewModel.saveCategory(0L, categoryName.trim(), "#6366F1")
                    }
                }
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}
