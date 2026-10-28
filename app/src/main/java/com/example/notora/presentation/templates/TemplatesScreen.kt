package com.example.notora.presentation.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import com.example.notora.core.designsystem.components.AppText
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
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.AppUtils
import com.example.notora.core.designsystem.components.AppButton
import com.example.notora.core.designsystem.components.AppCard
import com.example.notora.core.designsystem.components.AppCircularProgress
import com.example.notora.core.designsystem.components.AppEmptyState
import com.example.notora.core.designsystem.components.AppIconButton
import com.example.notora.core.designsystem.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    viewModel: TemplatesViewModel,
    onNavigateBack: () -> Unit,
    onNoteCreated: (noteId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val columns = AppUtils.getGridColumnCount(configuration, portraitColumns = 1, landscapeColumns = 2)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.templates_screen_title),
                onNavigateBack = onNavigateBack
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
        } else if (uiState.templates.isEmpty()) {
            AppEmptyState(
                icon = Icons.AutoMirrored.Filled.LibraryBooks,
                title = stringResource(R.string.templates_empty_title),
                description = uiState.errorMessage ?: stringResource(R.string.templates_empty_desc),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(AppDimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingDefault),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingDefault)
            ) {
                items(uiState.templates, key = { it.id }) { template ->
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppDimensions.cornerExtraLarge),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        contentPadding = PaddingValues(AppDimensions.paddingLarge)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = NotoraPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(AppDimensions.cornerSmall),
                                    modifier = Modifier.size(AppDimensions.components.avatar)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                            contentDescription = null,
                                            tint = NotoraPrimary,
                                            modifier = Modifier.size(AppDimensions.iconMedium)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(AppDimensions.paddingMedium))
                                Column {
                                    AppText(
                                        text = template.name,
                                        style = AppTypography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (template.isDefault) {
                                        AppText(
                                            text = "Standard Template",
                                            style = AppTypography.labelSmall,
                                            color = NotoraPrimary
                                        )
                                    }
                                }
                            }

                            if (!template.isDefault) {
                                AppIconButton(
                                    icon = Icons.Default.Delete,
                                    onClick = { viewModel.deleteTemplate(template.id) },
                                    contentDescription = stringResource(R.string.action_delete),
                                    tint = NotoraError.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
                        AppText(
                            text = template.description,
                            style = AppTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))
                        AppButton(
                            text = stringResource(R.string.templates_use_template),
                            onClick = {
                                viewModel.createNoteFromTemplate(template) { noteId ->
                                    onNoteCreated(noteId)
                                }
                            },
                            leadingIcon = Icons.Default.Add,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}
