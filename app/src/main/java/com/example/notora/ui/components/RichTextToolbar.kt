package com.example.notora.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notora.R
import com.example.notora.common.util.AppUtils
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.domain.editor.RichSpanType
import com.example.notora.domain.editor.RichTextEditorState
import com.example.notora.ui.components.toolbar.ActiveSubPanel
import com.example.notora.ui.components.toolbar.FormattingIconButton
import com.example.notora.ui.components.toolbar.HighlightColorSubPanel
import com.example.notora.ui.components.toolbar.LinkSubPanel
import com.example.notora.ui.components.toolbar.TextColorSubPanel
import com.example.notora.ui.components.toolbar.ToolGroupContainer
import com.example.notora.ui.components.toolbar.ToolbarDivider
import com.example.notora.ui.theme.NotoraCyan
import com.example.notora.ui.theme.NotoraPrimary

/**
 * Bottom-docked rich text formatting toolbar providing inline styles, block styles,
 * font coloring, highlighting, hyperlinks, and undo/redo operations.
 */
@Composable
fun RichTextToolbar(
    editorState: RichTextEditorState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var activePanel by remember { mutableStateOf(ActiveSubPanel.NONE) }
    var linkUrl by remember { mutableStateOf("") }

    val maxScroll = scrollState.maxValue
    val canScrollLeft = scrollState.value > 10
    val canScrollRight = maxScroll > 0 && scrollState.value < maxScroll - 10

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {

            // --- Sub-panel 1: Text Color Picker ---
            TextColorSubPanel(
                visible = activePanel == ActiveSubPanel.TEXT_COLOR,
                currentColorHex = editorState.getActiveTextColor(),
                onColorSelected = { hex -> editorState.applyTextColor(hex) },
                onResetColor = { editorState.applyTextColor(null) },
                onClose = { activePanel = ActiveSubPanel.NONE }
            )

            // --- Sub-panel 2: Highlight Color Picker ---
            HighlightColorSubPanel(
                visible = activePanel == ActiveSubPanel.HIGHLIGHT,
                currentHighlightHex = editorState.getActiveHighlightColor(),
                onHighlightSelected = { hex -> editorState.applyHighlightColor(hex) },
                onResetHighlight = { editorState.applyHighlightColor(null) },
                onClose = { activePanel = ActiveSubPanel.NONE }
            )

            // --- Sub-panel 3: Link Insertion ---
            LinkSubPanel(
                visible = activePanel == ActiveSubPanel.LINK,
                linkUrl = linkUrl,
                onLinkUrlChange = { linkUrl = it },
                onInsertLink = {
                    if (linkUrl.isNotBlank()) {
                        val validUrl = AppUtils.normalizeUrl(linkUrl)
                        editorState.insertLink(validUrl)
                        linkUrl = ""
                        activePanel = ActiveSubPanel.NONE
                    }
                },
                onClose = { activePanel = ActiveSubPanel.NONE }
            )

            // --- Main Tool Icons Row ---
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Group 1: Undo & Redo
                    ToolGroupContainer {
                        IconButton(
                            onClick = rememberDebouncedClick { editorState.undo() },
                            enabled = editorState.canUndo,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = stringResource(R.string.richtext_undo_desc),
                                modifier = Modifier.size(20.dp),
                                tint = if (editorState.canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        }

                        IconButton(
                            onClick = rememberDebouncedClick { editorState.redo() },
                            enabled = editorState.canRedo,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Redo,
                                contentDescription = stringResource(R.string.richtext_redo_desc),
                                modifier = Modifier.size(20.dp),
                                tint = if (editorState.canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        }
                    }

                    ToolbarDivider()

                    // Group 2: Inline Styles (Bold, Italic, Underline, Strikethrough, Code)
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.BOLD),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.BOLD) },
                            label = stringResource(R.string.richtext_bold_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatBold,
                                contentDescription = stringResource(R.string.richtext_bold_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.ITALIC),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.ITALIC) },
                            label = stringResource(R.string.richtext_italic_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatItalic,
                                contentDescription = stringResource(R.string.richtext_italic_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.UNDERLINE),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.UNDERLINE) },
                            label = stringResource(R.string.richtext_underline_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatUnderlined,
                                contentDescription = stringResource(R.string.richtext_underline_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.STRIKETHROUGH),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.STRIKETHROUGH) },
                            label = stringResource(R.string.richtext_strikethrough_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatStrikethrough,
                                contentDescription = stringResource(R.string.richtext_strikethrough_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.INLINE_CODE),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.INLINE_CODE) },
                            label = stringResource(R.string.richtext_code_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = stringResource(R.string.richtext_code_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    ToolbarDivider()

                    // Group 3: Headings (H1, H2, H3)
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.HEADING_1),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.HEADING_1) },
                            label = "H1"
                        ) {
                            Text("H1", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }

                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.HEADING_2),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.HEADING_2) },
                            label = "H2"
                        ) {
                            Text("H2", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.HEADING_3),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.HEADING_3) },
                            label = "H3"
                        ) {
                            Text("H3", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    ToolbarDivider()

                    // Group 4: Lists & Quotes
                    ToolGroupContainer {
                        FormattingIconButton(
                            isActive = false,
                            onClick = rememberDebouncedClick { editorState.toggleBulletList() },
                            label = stringResource(R.string.richtext_bullet_list_desc)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                contentDescription = stringResource(R.string.richtext_bullet_list_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FormattingIconButton(
                            isActive = false,
                            onClick = rememberDebouncedClick { editorState.toggleNumberedList() },
                            label = stringResource(R.string.richtext_numbered_list_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = stringResource(R.string.richtext_numbered_list_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FormattingIconButton(
                            isActive = editorState.isStyleActive(RichSpanType.BLOCK_QUOTE),
                            onClick = rememberDebouncedClick { editorState.toggleSpanStyle(RichSpanType.BLOCK_QUOTE) },
                            label = stringResource(R.string.richtext_quote_desc)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = stringResource(R.string.richtext_quote_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    ToolbarDivider()

                    // Group 5: Text Color, Highlight, Link & Clear
                    ToolGroupContainer {
                        // Text Color Button with active color indicator bar
                        val activeColor = editorState.getActiveTextColor()
                        IconButton(
                            onClick = rememberDebouncedClick {
                                activePanel = if (activePanel == ActiveSubPanel.TEXT_COLOR) ActiveSubPanel.NONE else ActiveSubPanel.TEXT_COLOR
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (activePanel == ActiveSubPanel.TEXT_COLOR) NotoraPrimary.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatColorText,
                                    contentDescription = stringResource(R.string.richtext_text_color_desc),
                                    tint = AppUtils.parseHexColor(activeColor, fallback = NotoraPrimary),
                                    modifier = Modifier.size(19.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(width = 14.dp, height = 3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(AppUtils.parseHexColor(activeColor, fallback = NotoraPrimary))
                                )
                            }
                        }

                        // Highlight Marker Button
                        val activeHighlight = editorState.getActiveHighlightColor()
                        IconButton(
                            onClick = rememberDebouncedClick {
                                activePanel = if (activePanel == ActiveSubPanel.HIGHLIGHT) ActiveSubPanel.NONE else ActiveSubPanel.HIGHLIGHT
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (activePanel == ActiveSubPanel.HIGHLIGHT) NotoraCyan.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatColorFill,
                                    contentDescription = stringResource(R.string.richtext_highlight_desc),
                                    tint = AppUtils.parseHexColor(activeHighlight, fallback = NotoraCyan),
                                    modifier = Modifier.size(19.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(width = 14.dp, height = 3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(AppUtils.parseHexColor(activeHighlight, fallback = NotoraCyan))
                                )
                            }
                        }

                        // Link Button
                        IconButton(
                            onClick = rememberDebouncedClick {
                                activePanel = if (activePanel == ActiveSubPanel.LINK) ActiveSubPanel.NONE else ActiveSubPanel.LINK
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (activePanel == ActiveSubPanel.LINK) NotoraPrimary.copy(alpha = 0.15f) else Color.Transparent
                            ),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = stringResource(R.string.richtext_insert_link),
                                modifier = Modifier.size(20.dp),
                                tint = if (editorState.isStyleActive(RichSpanType.LINK)) NotoraPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Clear Formatting Button
                        IconButton(
                            onClick = rememberDebouncedClick { editorState.clearSelectionFormatting() },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatClear,
                                contentDescription = stringResource(R.string.richtext_clear_format_desc),
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Left & Right Scroll Affordance Indicators
                if (canScrollLeft) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(width = 24.dp, height = 48.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceContainer,
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = stringResource(R.string.richtext_scroll_left_desc),
                            tint = NotoraPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (canScrollRight) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(width = 24.dp, height = 48.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surfaceContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = stringResource(R.string.richtext_scroll_right_desc),
                            tint = NotoraPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Visible Horizontal Scrollbar Track & Pill Thumb
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 3.dp)
            ) {
                val totalWidth = maxWidth
                val thumbWidth = 48.dp
                val availableTravel = totalWidth - thumbWidth
                val scrollFraction = if (maxScroll > 0) scrollState.value.toFloat() / maxScroll else 0f
                val thumbOffset = availableTravel * scrollFraction

                // Track Background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                ) {
                    // Sliding Thumb Indicator
                    Box(
                        modifier = Modifier
                            .offset(x = thumbOffset)
                            .width(thumbWidth)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(NotoraPrimary, NotoraCyan)
                                )
                            )
                    )
                }
            }
        }
    }
}
