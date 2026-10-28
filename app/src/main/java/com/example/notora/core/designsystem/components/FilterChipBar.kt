package com.example.notora.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.NotoraAmber
import com.example.notora.core.designsystem.NotoraCyan
import com.example.notora.core.designsystem.NotoraError
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.NotoraRose
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.NoteFilter
import com.example.notora.domain.model.Tag

@Composable
fun FilterChipBar(
    filter: NoteFilter,
    categories: List<Category>,
    tags: List<Tag>,
    onFilterChange: (NoteFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilter = filter.isPinnedOnly || filter.isFavoriteOnly || filter.categoryId != null || filter.tagId != null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = AppDimensions.paddingDefault, vertical = AppDimensions.paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
    ) {
        // Clear Filter Button if active
        AnimatedVisibility(
            visible = hasActiveFilter,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AppFilterChip(
                selected = true,
                onClick = {
                    onFilterChange(
                        filter.copy(
                            categoryId = null,
                            tagId = null,
                            isPinnedOnly = false,
                            isFavoriteOnly = false
                        )
                    )
                },
                label = stringResource(R.string.action_clear),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        tint = NotoraPrimary
                    )
                }
            )
        }

        // Pinned Filter Chip
        AppFilterChip(
            selected = filter.isPinnedOnly,
            onClick = { onFilterChange(filter.copy(isPinnedOnly = !filter.isPinnedOnly)) },
            label = stringResource(R.string.action_pin),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                    tint = if (filter.isPinnedOnly) NotoraPrimary else NotoraAmber
                )
            }
        )

        // Favorite Filter Chip
        AppFilterChip(
            selected = filter.isFavoriteOnly,
            onClick = { onFilterChange(filter.copy(isFavoriteOnly = !filter.isFavoriteOnly)) },
            label = stringResource(R.string.action_favorite),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                    tint = if (filter.isFavoriteOnly) NotoraPrimary else NotoraRose
                )
            }
        )

        // Category Chips
        categories.forEach { category ->
            val isSelected = filter.categoryId == category.id
            AppFilterChip(
                selected = isSelected,
                onClick = {
                    val newCatId = if (isSelected) null else category.id
                    onFilterChange(filter.copy(categoryId = newCatId))
                },
                label = category.name,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        tint = if (isSelected) NotoraPrimary else NotoraCyan
                    )
                }
            )
        }

        // Tag Chips
        tags.forEach { tag ->
            val isSelected = filter.tagId == tag.id
            AppFilterChip(
                selected = isSelected,
                onClick = {
                    val newTagId = if (isSelected) null else tag.id
                    onFilterChange(filter.copy(tagId = newTagId))
                },
                label = "#${tag.name}",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        tint = if (isSelected) NotoraPrimary else NotoraError
                    )
                }
            )
        }
    }
}
