package com.example.notora.presentation.editor

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NoteColorPalette
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.AppUtils
import com.example.notora.domain.model.NoteType
import com.example.notora.core.designsystem.components.AppText
import com.example.notora.core.designsystem.components.ChecklistComponent
import com.example.notora.core.designsystem.components.ColorPickerBottomSheet
import com.example.notora.core.designsystem.components.RichTextToolbar
import com.example.notora.presentation.editor.components.EditorAttachmentsSection
import com.example.notora.presentation.editor.components.EditorMetaChips
import com.example.notora.presentation.editor.components.EditorTopBar
import kotlinx.coroutines.launch

/**
 * Screen for viewing, authoring, and formatting notes and checklists with support for
 * rich text styles, attachments, categories, tags, and landscape dual-pane responsive layout.
 */
@Composable
fun NoteEditorScreen(
    viewModel: NoteEditorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRevisions: (noteId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)

    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    val bgColor = NoteColorPalette.getBackgroundColor(uiState.colorHex)

    val visualTransformation = remember(viewModel.editorState.spans.size) {
        RichTextVisualTransformation(viewModel.editorState)
    }

    BackHandler {
        scope.launch {
            viewModel.handleBackPress()
            onNavigateBack()
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.addAttachmentUri(it) }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            EditorTopBar(
                isPinned = uiState.isPinned,
                isFavorite = uiState.isFavorite,
                noteId = uiState.noteId,
                backgroundColor = bgColor,
                onBackClick = {
                    scope.launch {
                        viewModel.handleBackPress()
                        onNavigateBack()
                    }
                },
                onTogglePin = { viewModel.togglePin() },
                onToggleFavorite = { viewModel.toggleFavorite() },
                onOpenColorPicker = { showColorPicker = true },
                onAttachFileClick = { documentPickerLauncher.launch(arrayOf("*/*")) },
                onNavigateToRevisions = onNavigateToRevisions,
                onDeleteNote = {
                    viewModel.deleteNote()
                    onNavigateBack()
                }
            )
        },
        bottomBar = {
            RichTextToolbar(
                editorState = viewModel.editorState,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppDimensions.paddingExtraLarge)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .padding(end = AppDimensions.paddingDefault)
                        .verticalScroll(rememberScrollState())
                ) {
                    BasicTextField(
                        value = viewModel.titleTextFieldValue,
                        onValueChange = { viewModel.onTitleTextFieldValueChange(it) },
                        textStyle = AppTypography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = SolidColor(NotoraPrimary),
                        decorationBox = { innerTextField ->
                            Box {
                                if (viewModel.titleTextFieldValue.text.isEmpty()) {
                                    AppText(
                                        text = stringResource(R.string.editor_title_placeholder),
                                        style = AppTypography.headlineMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))

                    BasicTextField(
                        value = viewModel.editorState.textFieldValue,
                        onValueChange = {
                            viewModel.editorState.updateTextFieldValue(it)
                            viewModel.onContentChange()
                        },
                        visualTransformation = visualTransformation,
                        textStyle = AppTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = SolidColor(NotoraPrimary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (viewModel.editorState.textFieldValue.text.isEmpty()) {
                                    AppText(
                                        text = stringResource(R.string.editor_content_placeholder),
                                        style = AppTypography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                        .padding(start = AppDimensions.paddingDefault)
                        .verticalScroll(rememberScrollState())
                ) {
                    EditorMetaChips(
                        category = uiState.category,
                        availableCategories = uiState.availableCategories,
                        tags = uiState.tags,
                        availableTags = uiState.availableTags,
                        wordCount = uiState.wordCount,
                        readingTimeMinutes = uiState.readingTimeMinutes,
                        onSelectCategory = { viewModel.setCategory(it) },
                        onToggleTag = { viewModel.toggleTag(it) }
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))

                    if (uiState.noteType == NoteType.CHECKLIST || uiState.checklistItems.isNotEmpty()) {
                        ChecklistComponent(
                            items = uiState.checklistItems,
                            onItemChange = { viewModel.setChecklistItems(it) }
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))
                    }

                    EditorAttachmentsSection(
                        attachments = uiState.attachments,
                        onRemoveAttachment = { viewModel.removeAttachment(it) }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppDimensions.paddingLarge)
                    .verticalScroll(rememberScrollState())
            ) {
                EditorMetaChips(
                    category = uiState.category,
                    availableCategories = uiState.availableCategories,
                    tags = uiState.tags,
                    availableTags = uiState.availableTags,
                    wordCount = uiState.wordCount,
                    readingTimeMinutes = uiState.readingTimeMinutes,
                    onSelectCategory = { viewModel.setCategory(it) },
                    onToggleTag = { viewModel.toggleTag(it) }
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))

                BasicTextField(
                    value = viewModel.titleTextFieldValue,
                    onValueChange = { viewModel.onTitleTextFieldValueChange(it) },
                    textStyle = AppTypography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(NotoraPrimary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (viewModel.titleTextFieldValue.text.isEmpty()) {
                                AppText(
                                    text = stringResource(R.string.editor_title_placeholder),
                                    style = AppTypography.headlineMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))

                BasicTextField(
                    value = viewModel.editorState.textFieldValue,
                    onValueChange = {
                        viewModel.editorState.updateTextFieldValue(it)
                        viewModel.onContentChange()
                    },
                    visualTransformation = visualTransformation,
                    textStyle = AppTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(NotoraPrimary),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (viewModel.editorState.textFieldValue.text.isEmpty()) {
                                AppText(
                                    text = stringResource(R.string.editor_content_placeholder),
                                    style = AppTypography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))

                if (uiState.noteType == NoteType.CHECKLIST || uiState.checklistItems.isNotEmpty()) {
                    ChecklistComponent(
                        items = uiState.checklistItems,
                        onItemChange = { viewModel.setChecklistItems(it) }
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingDefault))
                }

                EditorAttachmentsSection(
                    attachments = uiState.attachments,
                    onRemoveAttachment = { viewModel.removeAttachment(it) }
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraLarge))
            }
        }

        if (showColorPicker) {
            ColorPickerBottomSheet(
                selectedColorHex = uiState.colorHex,
                onColorSelected = { viewModel.setColorHex(it) },
                onDismissRequest = { showColorPicker = false }
            )
        }
    }
}
