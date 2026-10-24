package com.example.notora.ui.templates

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.ui.components.common.AppButton
import com.example.notora.ui.components.common.AppEmptyState
import com.example.notora.ui.components.common.AppTopBar
import com.example.notora.ui.theme.AppDimensions
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.NotoraError
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.sdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    viewModel: TemplatesViewModel,
    onNavigateBack: () -> Unit,
    onNoteCreated: (noteId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val templates by viewModel.templates.collectAsState()
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
        if (templates.isEmpty()) {
            AppEmptyState(
                icon = Icons.AutoMirrored.Filled.LibraryBooks,
                title = stringResource(R.string.templates_empty_title),
                description = stringResource(R.string.templates_empty_desc),
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
                horizontalArrangement = Arrangement.spacedBy(14.sdp),
                verticalArrangement = Arrangement.spacedBy(14.sdp)
            ) {
                items(templates, key = { it.id }) { template ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppDimensions.radiusExtraLarge),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.sdp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = NotoraPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(AppDimensions.radiusSmall),
                                        modifier = Modifier.size(38.sdp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                                contentDescription = null,
                                                tint = NotoraPrimary,
                                                modifier = Modifier.size(20.sdp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.sdp))
                                    Column {
                                        Text(
                                            text = template.name,
                                            style = AppTypography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (template.isDefault) {
                                            Text(
                                                text = "Standard Template",
                                                style = AppTypography.labelSmall,
                                                color = NotoraPrimary
                                            )
                                        }
                                    }
                                }

                                if (!template.isDefault) {
                                    IconButton(onClick = rememberDebouncedClick { viewModel.deleteTemplate(template.id) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.action_delete),
                                            tint = NotoraError.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.sdp))
                            Text(
                                text = template.description,
                                style = AppTypography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.sdp))
                            AppButton(
                                text = stringResource(R.string.templates_use_template),
                                onClick = rememberDebouncedClick {
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
}
