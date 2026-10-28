package com.example.notora.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.domain.editor.RichSpanType
import com.example.notora.domain.editor.RichTextEditorState
import com.example.notora.core.designsystem.components.toolbar.ActiveSubPanel
import com.example.notora.core.designsystem.components.toolbar.FormattingIconButton
import com.example.notora.core.designsystem.components.toolbar.HighlightColorSubPanel
import com.example.notora.core.designsystem.components.toolbar.LinkSubPanel
import com.example.notora.core.designsystem.components.toolbar.TextColorSubPanel
import com.example.notora.core.designsystem.components.toolbar.ToolGroupContainer
import com.example.notora.core.designsystem.components.toolbar.ToolbarDivider

/**
 * Bottom-docked rich text formatting toolbar providing inline styles, block styles,
 * font coloring, highlighting, hyperlinks, and undo/redo operations.
 */
@Composable
fun RichTextToolbar(
    editorState: RichTextEditorState,
    modifier: Modifier = Modifier
) {
    var activeSubPanel by remember { mutableStateOf(ActiveSubPanel.NONE) }
    var linkInputUrl by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val currentSpans = editorState.spans
    val selection = editorState.textFieldValue.selection
    val isBoldActive = currentSpans.any { it.styleType == RichSpanType.BOLD && it.overlaps(selection.start, selection.end) }
    val isItalicActive = currentSpans.any { it.styleType == RichSpanType.ITALIC && it.overlaps(selection.start, selection.end) }
    val isUnderlineActive = currentSpans.any { it.styleType == RichSpanType.UNDERLINE && it.overlaps(selection.start, selection.end) }
    val isStrikethroughActive = currentSpans.any { it.styleType == RichSpanType.STRIKETHROUGH && it.overlaps(selection.start, selection.end) }
    val isH1Active = currentSpans.any { it.styleType == RichSpanType.HEADING_1 && it.overlaps(selection.start, selection.end) }
    val isH2Active = currentSpans.any { it.styleType == RichSpanType.HEADING_2 && it.overlaps(selection.start, selection.end) }
    val isH3Active = currentSpans.any { it.styleType == RichSpanType.HEADING_3 && it.overlaps(selection.start, selection.end) }
    val isCodeActive = currentSpans.any { it.styleType == RichSpanType.INLINE_CODE && it.overlaps(selection.start, selection.end) }
    val isQuoteActive = currentSpans.any { it.styleType == RichSpanType.BLOCK_QUOTE && it.overlaps(selection.start, selection.end) }
    val isBulletActive = currentSpans.any { it.styleType == RichSpanType.BULLET_LIST_ITEM && it.overlaps(selection.start, selection.end) }
    val isNumberedActive = currentSpans.any { it.styleType == RichSpanType.NUMBERED_LIST_ITEM && it.overlaps(selection.start, selection.end) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = AppDimensions.elevations.medium,
        shadowElevation = AppDimensions.elevations.high,
        border = BorderStroke(AppDimensions.borders.thin, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Secondary Active SubPanel (Color / Highlight / Link)
            TextColorSubPanel(
                visible = activeSubPanel == ActiveSubPanel.TEXT_COLOR,
                currentColorHex = currentSpans.firstOrNull { it.styleType == RichSpanType.TEXT_COLOR && it.overlaps(selection.start, selection.end) }?.value,
                onColorSelected = { hex -> editorState.toggleSpanStyle(RichSpanType.TEXT_COLOR, hex) },
                onResetColor = { editorState.toggleSpanStyle(RichSpanType.TEXT_COLOR, null) },
                onClose = { activeSubPanel = ActiveSubPanel.NONE }
            )

            HighlightColorSubPanel(
                visible = activeSubPanel == ActiveSubPanel.HIGHLIGHT,
                currentHighlightHex = currentSpans.firstOrNull { it.styleType == RichSpanType.HIGHLIGHT_COLOR && it.overlaps(selection.start, selection.end) }?.value,
                onHighlightSelected = { hex -> editorState.toggleSpanStyle(RichSpanType.HIGHLIGHT_COLOR, hex) },
                onResetHighlight = { editorState.toggleSpanStyle(RichSpanType.HIGHLIGHT_COLOR, null) },
                onClose = { activeSubPanel = ActiveSubPanel.NONE }
            )

            LinkSubPanel(
                visible = activeSubPanel == ActiveSubPanel.LINK,
                linkUrl = linkInputUrl,
                onLinkUrlChange = { linkInputUrl = it },
                onInsertLink = {
                    if (linkInputUrl.isNotBlank()) {
                        editorState.toggleSpanStyle(RichSpanType.LINK, linkInputUrl.trim())
                        linkInputUrl = ""
                        activeSubPanel = ActiveSubPanel.NONE
                    }
                },
                onClose = {
                    linkInputUrl = ""
                    activeSubPanel = ActiveSubPanel.NONE
                }
            )

            // Main Horizontal Formatting Toolbar
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingMicro),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMicro)
                ) {
                    // Undo / Redo Group
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = false,
                            onClick = { editorState.undo() },
                            label = stringResource(R.string.richtext_undo_desc)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = stringResource(R.string.richtext_undo_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = false,
                            onClick = { editorState.redo() },
                            label = stringResource(R.string.richtext_redo_desc)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Redo,
                                contentDescription = stringResource(R.string.richtext_redo_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }
                    }

                    ToolbarDivider()

                    // Inline Styles Group (Bold, Italic, Underline, Strikethrough)
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = isBoldActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.BOLD) },
                            label = stringResource(R.string.richtext_bold_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatBold,
                                contentDescription = stringResource(R.string.richtext_bold_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = isItalicActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.ITALIC) },
                            label = stringResource(R.string.richtext_italic_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatItalic,
                                contentDescription = stringResource(R.string.richtext_italic_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = isUnderlineActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.UNDERLINE) },
                            label = stringResource(R.string.richtext_underline_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatUnderlined,
                                contentDescription = stringResource(R.string.richtext_underline_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = isStrikethroughActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.STRIKETHROUGH) },
                            label = stringResource(R.string.richtext_strikethrough_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatStrikethrough,
                                contentDescription = stringResource(R.string.richtext_strikethrough_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }
                    }

                    ToolbarDivider()

                    // Headings Group (H1, H2, H3)
                    ToolGroupContainer {
                        HeadingToggleButton(
                            text = "H1",
                            isActive = isH1Active,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.HEADING_1) }
                        )
                        HeadingToggleButton(
                            text = "H2",
                            isActive = isH2Active,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.HEADING_2) }
                        )
                        HeadingToggleButton(
                            text = "H3",
                            isActive = isH3Active,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.HEADING_3) }
                        )
                    }

                    ToolbarDivider()

                    // Color & Highlight Group
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = activeSubPanel == ActiveSubPanel.TEXT_COLOR,
                            onClick = {
                                activeSubPanel = if (activeSubPanel == ActiveSubPanel.TEXT_COLOR) ActiveSubPanel.NONE else ActiveSubPanel.TEXT_COLOR
                            },
                            label = stringResource(R.string.richtext_text_color_title)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatColorText,
                                contentDescription = stringResource(R.string.richtext_text_color_title),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = activeSubPanel == ActiveSubPanel.HIGHLIGHT,
                            onClick = {
                                activeSubPanel = if (activeSubPanel == ActiveSubPanel.HIGHLIGHT) ActiveSubPanel.NONE else ActiveSubPanel.HIGHLIGHT
                            },
                            label = stringResource(R.string.richtext_highlight_title)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatColorFill,
                                contentDescription = stringResource(R.string.richtext_highlight_title),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }
                    }

                    ToolbarDivider()

                    // Block & List Group
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = isCodeActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.INLINE_CODE) },
                            label = stringResource(R.string.richtext_code_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = stringResource(R.string.richtext_code_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = isQuoteActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.BLOCK_QUOTE) },
                            label = stringResource(R.string.richtext_quote_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = stringResource(R.string.richtext_quote_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = isBulletActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.BULLET_LIST_ITEM) },
                            label = stringResource(R.string.richtext_bullet_list_desc)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                contentDescription = stringResource(R.string.richtext_bullet_list_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        FormattingIconButton(
                            isActive = isNumberedActive,
                            onClick = { editorState.toggleSpanStyle(RichSpanType.NUMBERED_LIST_ITEM) },
                            label = stringResource(R.string.richtext_numbered_list_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = stringResource(R.string.richtext_numbered_list_desc),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }
                    }

                    ToolbarDivider()

                    // Link Group
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = activeSubPanel == ActiveSubPanel.LINK,
                            onClick = {
                                activeSubPanel = if (activeSubPanel == ActiveSubPanel.LINK) ActiveSubPanel.NONE else ActiveSubPanel.LINK
                            },
                            label = stringResource(R.string.richtext_insert_web_link)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = stringResource(R.string.richtext_insert_web_link),
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeadingToggleButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    FormattingIconButton(
        isActive = isActive,
        onClick = onClick,
        label = text
    ) {
        AppText(
            text = text,
            style = AppTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
