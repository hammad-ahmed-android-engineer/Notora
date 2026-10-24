package com.example.notora.ui.revisions

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.common.util.clickableDebounced
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.ui.components.common.AppButton
import com.example.notora.ui.components.common.AppEmptyState
import com.example.notora.ui.components.common.AppTopBar
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.sdp

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NoteRevisionsScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteRevisionsViewModel = hiltViewModel()
) {
    val revisions by viewModel.getRevisions(noteId).collectAsState()
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
        if (revisions.isEmpty()) {
            AppEmptyState(
                icon = Icons.Default.History,
                title = stringResource(R.string.revisions_empty_title),
                description = stringResource(R.string.revisions_empty_desc),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            val activeSelected = revisions.find { it.id == selectedRevisionId } ?: revisions.firstOrNull()

            if (isLandscape && activeSelected != null) {
                // Landscape Split: Revisions list on left, Preview/Restore on right
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(AppDimensions.paddingMedium)
                ) {
                    // Left Pane: Revision Snapshots List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.sdp)
                    ) {
                        items(revisions, key = { it.id }) { revision ->
                            val isSelected = activeSelected.id == revision.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickableDebounced(debounceInterval = 500L) { selectedRevisionId = revision.id },
                                shape = RoundedCornerShape(AppDimensions.radiusLarge),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) NotoraPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.sdp)) {
                                    Text(
                                        text = AppUtils.formatDateTime(revision.createdAt),
                                        style = AppTypography.labelMedium,
                                        color = if (isSelected) NotoraPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.sdp))
                                    Text(
                                        text = revision.title.ifBlank { stringResource(R.string.revisions_untitled) },
                                        style = AppTypography.bodyMedium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(AppDimensions.paddingMedium))

                    // Right Pane: Full Content Preview & Restore Button
                    val doc = RichTextSerializer.fromJson(activeSelected.contentJson)
                    Card(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(AppDimensions.radiusLarge),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(18.sdp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = AppUtils.formatDateTime(activeSelected.createdAt),
                                    style = AppTypography.labelMedium,
                                    color = NotoraPrimary
                                )

                                AppButton(
                                    text = stringResource(R.string.revisions_restore_button),
                                    onClick = rememberDebouncedClick {
                                        viewModel.restoreRevision(noteId, activeSelected) {
                                            onNavigateBack()
                                        }
                                    },
                                    leadingIcon = Icons.Default.Restore
                                )
                            }

                            Spacer(modifier = Modifier.height(14.sdp))
                            if (activeSelected.title.isNotBlank()) {
                                Text(
                                    text = activeSelected.title,
                                    style = AppTypography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.sdp))
                            }

                            Text(
                                text = doc.text,
                                style = AppTypography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Portrait Vertical List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(AppDimensions.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(14.sdp)
                ) {
                    items(revisions, key = { it.id }) { revision ->
                        val doc = RichTextSerializer.fromJson(revision.contentJson)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(AppDimensions.radiusLarge),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(AppDimensions.paddingMedium)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = AppUtils.formatDateTime(revision.createdAt),
                                        style = AppTypography.labelMedium,
                                        color = NotoraPrimary
                                    )

                                    AppButton(
                                        text = stringResource(R.string.revisions_restore_button),
                                        onClick = rememberDebouncedClick {
                                            viewModel.restoreRevision(noteId, revision) {
                                                onNavigateBack()
                                            }
                                        },
                                        leadingIcon = Icons.Default.Restore
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.sdp))
                                if (revision.title.isNotBlank()) {
                                    Text(
                                        text = revision.title,
                                        style = AppTypography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.sdp))
                                }

                                Text(
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
}
