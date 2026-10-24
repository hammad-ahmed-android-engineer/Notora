package com.example.notora.ui.editor

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.domain.model.NoteType
import com.example.notora.ui.components.ChecklistComponent
import com.example.notora.ui.components.ColorPickerBottomSheet
import com.example.notora.ui.components.RichTextToolbar
import com.example.notora.ui.editor.components.EditorAttachmentsSection
import com.example.notora.ui.editor.components.EditorMetaChips
import com.example.notora.ui.editor.components.EditorTopBar
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NoteColorPalette
import com.example.notora.ui.theme.NotoraPrimary
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
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = AppUtils.isLandscape(configuration)

    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    val bgColor = NoteColorPalette.getBackgroundColor(uiState.colorHex)

    val visualTransformation = remember(viewModel.editorState.spans.size) {
        RichTextVisualTransformation(viewModel.editorState)
    }

    // Predictive Back Handler - Clean up empty notes or save valid notes
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
            // Landscape Dual-Column Responsive Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                // Left Pane (60%): Main Canvas (Title + Rich Content)
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .padding(end = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    BasicTextField(
                        value = viewModel.titleTextFieldValue,
                        onValueChange = { viewModel.onTitleTextFieldValueChange(it) },
                        textStyle = AppTypography.headlineMedium.copy(color = Color(0xFF0F172A)),
                        cursorBrush = SolidColor(NotoraPrimary),
                        decorationBox = { innerTextField ->
                            Box {
                                if (viewModel.titleTextFieldValue.text.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.editor_title_placeholder),
                                        style = AppTypography.headlineMedium.copy(
                                            color = Color(0xA6475569)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BasicTextField(
                        value = viewModel.editorState.textFieldValue,
                        onValueChange = {
                            viewModel.editorState.updateTextFieldValue(it)
                            viewModel.onContentChange()
                        },
                        visualTransformation = visualTransformation,
                        textStyle = AppTypography.bodyLarge.copy(color = Color(0xFF0F172A)),
                        cursorBrush = SolidColor(NotoraPrimary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (viewModel.editorState.textFieldValue.text.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.editor_content_placeholder),
                                        style = AppTypography.bodyLarge.copy(
                                            color = Color(0xA6475569)
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

                // Right Pane (40%): Metadata, Category, Tags, Checklist, Attachments
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                        .padding(start = 16.dp)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.noteType == NoteType.CHECKLIST || uiState.checklistItems.isNotEmpty()) {
                        ChecklistComponent(
                            items = uiState.checklistItems,
                            onItemChange = { viewModel.setChecklistItems(it) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    EditorAttachmentsSection(
                        attachments = uiState.attachments,
                        onRemoveAttachment = { viewModel.removeAttachment(it) }
                    )
                }
            }
        } else {
            // Portrait Layout: Streamlined Vertical Flow
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
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

                Spacer(modifier = Modifier.height(12.dp))

                BasicTextField(
                    value = viewModel.titleTextFieldValue,
                    onValueChange = { viewModel.onTitleTextFieldValueChange(it) },
                    textStyle = AppTypography.headlineMedium.copy(color = Color(0xFF0F172A)),
                    cursorBrush = SolidColor(NotoraPrimary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (viewModel.titleTextFieldValue.text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.editor_title_placeholder),
                                    style = AppTypography.headlineMedium.copy(
                                        color = Color(0xA6475569)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = viewModel.editorState.textFieldValue,
                    onValueChange = {
                        viewModel.editorState.updateTextFieldValue(it)
                        viewModel.onContentChange()
                    },
                    visualTransformation = visualTransformation,
                    textStyle = AppTypography.bodyLarge.copy(color = Color(0xFF0F172A)),
                    cursorBrush = SolidColor(NotoraPrimary),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (viewModel.editorState.textFieldValue.text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.editor_content_placeholder),
                                    style = AppTypography.bodyLarge.copy(
                                        color = Color(0xA6475569)
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

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.noteType == NoteType.CHECKLIST || uiState.checklistItems.isNotEmpty()) {
                    ChecklistComponent(
                        items = uiState.checklistItems,
                        onItemChange = { viewModel.setChecklistItems(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                EditorAttachmentsSection(
                    attachments = uiState.attachments,
                    onRemoveAttachment = { viewModel.removeAttachment(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))
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
