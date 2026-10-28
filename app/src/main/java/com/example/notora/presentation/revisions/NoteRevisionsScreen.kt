package com.example.notora.presentation.revisions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.AppUtils
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.core.designsystem.components.AppButton
import com.example.notora.core.designsystem.components.AppCard
import com.example.notora.core.designsystem.components.AppCircularProgress
import com.example.notora.core.designsystem.components.AppEmptyState
import com.example.notora.core.designsystem.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteRevisionsScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteRevisionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.getRevisions(noteId).collectAsStateWithLifecycle()
    var selectedRevisionId by rememberSaveable { mutableStateOf<Long?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.revisions_screen_title),
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
        } else if (uiState.revisions.isEmpty()) {
            AppEmptyState(
                icon = Icons.Default.History,
                title = stringResource(R.string.revisions_empty_title),
                description = uiState.errorMessage ?: stringResource(R.string.revisions_empty_desc),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            val revisions = uiState.revisions
            val activeSelected = revisions.find { it.id == selectedRevisionId } ?: revisions.firstOrNull()

            if (isLandscape && activeSelected != null) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(AppDimensions.paddingMedium)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
                    ) {
                        items(revisions, key = { it.id }) { revision ->
                            val isSelected = activeSelected.id == revision.id
                            AppCard(
                                onClick = { selectedRevisionId = revision.id },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(AppDimensions.cornerLarge),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) NotoraPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                                ),
                                contentPadding = PaddingValues(AppDimensions.paddingDefault)
                            ) {
                                AppText(
                                    text = AppUtils.formatDateTime(revision.createdAt),
                                    style = AppTypography.labelMedium,
                                    color = if (isSelected) NotoraPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                AppText(
                                    text = revision.title.ifBlank { stringResource(R.string.revisions_untitled) },
                                    style = AppTypography.bodyMedium,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(AppDimensions.paddingMedium))

                    val doc = RichTextSerializer.fromJson(activeSelected.contentJson)
                    AppCard(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(AppDimensions.cornerLarge),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(AppDimensions.paddingLarge)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppText(
                                    text = AppUtils.formatDateTime(activeSelected.createdAt),
                                    style = AppTypography.labelMedium,
                                    color = NotoraPrimary
                                )

                                AppButton(
                                    text = stringResource(R.string.revisions_restore_button),
                                    onClick = {
                                        viewModel.restoreRevision(noteId, activeSelected) {
                                            onNavigateBack()
                                        }
                                    },
                                    leadingIcon = Icons.Default.Restore
                                )
                            }

                            Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))
                            if (activeSelected.title.isNotBlank()) {
                                AppText(
                                    text = activeSelected.title,
                                    style = AppTypography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                            }

                            AppText(
                                text = doc.text,
                                style = AppTypography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(AppDimensions.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingDefault)
                ) {
                    items(revisions, key = { it.id }) { revision ->
                        val doc = RichTextSerializer.fromJson(revision.contentJson)
                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(AppDimensions.cornerLarge),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            contentPadding = PaddingValues(AppDimensions.paddingMedium)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppText(
                                    text = AppUtils.formatDateTime(revision.createdAt),
                                    style = AppTypography.labelMedium,
                                    color = NotoraPrimary
                                )

                                AppButton(
                                    text = stringResource(R.string.revisions_restore_button),
                                    onClick = {
                                        viewModel.restoreRevision(noteId, revision) {
                                            onNavigateBack()
                                        }
                                    },
                                    leadingIcon = Icons.Default.Restore
                                )
                            }

                            Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
                            if (revision.title.isNotBlank()) {
                                AppText(
                                    text = revision.title,
                                    style = AppTypography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                            }

                            AppText(
                                text = AppUtils.truncate(doc.text, 220),
                                style = AppTypography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
