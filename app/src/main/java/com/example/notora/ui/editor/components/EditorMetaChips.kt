package com.example.notora.ui.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.Tag
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary

/**
 * Metadata chips displaying category folder selection, reading time / word count statistics,
 * and available tags.
 */
@Composable
fun EditorMetaChips(
    category: Category?,
    availableCategories: List<Category>,
    tags: List<Tag>,
    availableTags: List<Tag>,
    wordCount: Int,
    readingTimeMinutes: Int,
    onSelectCategory: (Category?) -> Unit,
    onToggleTag: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCategoryMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box {
                FilterChip(
                    selected = category != null,
                    onClick = rememberDebouncedClick { showCategoryMenu = true },
                    label = {
                        Text(
                            text = category?.name ?: stringResource(R.string.editor_select_folder),
                            style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = NotoraPrimary.copy(alpha = 0.1f),
                        labelColor = NotoraPrimary,
                        iconColor = NotoraPrimary,
                        selectedContainerColor = NotoraPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = NotoraPrimary,
                        selectedLeadingIconColor = NotoraPrimary
                    )
                )

                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.editor_no_folder), style = AppTypography.bodyMedium) },
                        onClick = rememberDebouncedClick {
                            showCategoryMenu = false
                            onSelectCategory(null)
                        }
                    )
                    availableCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name, style = AppTypography.bodyMedium) },
                            onClick = rememberDebouncedClick {
                                showCategoryMenu = false
                                onSelectCategory(cat)
                            }
                        )
                    }
                }
            }

            Surface(
                color = NotoraPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.editor_word_and_reading_time, wordCount, readingTimeMinutes),
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = NotoraPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        if (availableTags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(availableTags) { tag ->
                    val isSelected = tags.any { it.id == tag.id }
                    FilterChip(
                        selected = isSelected,
                        onClick = rememberDebouncedClick { onToggleTag(tag) },
                        label = { Text("#${tag.name}", style = AppTypography.labelSmall) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = NotoraPrimary.copy(alpha = 0.1f),
                            labelColor = NotoraPrimary,
                            selectedContainerColor = NotoraPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = NotoraPrimary
                        )
                    )
                }
            }
        }
    }
}
