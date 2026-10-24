package com.example.notora.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.NoteFilter
import com.example.notora.domain.model.Tag
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraCyan
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.ui.theme.NotoraRose
import com.example.notora.ui.theme.NotoraError

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
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Clear All Filters Chip (Animated)
        AnimatedVisibility(
            visible = hasActiveFilter,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FilterChip(
                selected = false,
                onClick = rememberDebouncedClick {
                    onFilterChange(
                        filter.copy(
                            isPinnedOnly = false,
                            isFavoriteOnly = false,
                            categoryId = null,
                            tagId = null
                        )
                    )
                },
                label = { Text(stringResource(R.string.action_clear_all), style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_clear_all),
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = NotoraError.copy(alpha = 0.15f),
                    labelColor = NotoraError
                )
            )
        }

        // Pinned Filter
        FilterChip(
            selected = filter.isPinnedOnly,
            onClick = rememberDebouncedClick { onFilterChange(filter.copy(isPinnedOnly = !filter.isPinnedOnly)) },
            label = { Text(stringResource(R.string.home_filter_pinned), style = AppTypography.labelMedium) },
            leadingIcon = {
                Icon(
                    Icons.Default.PushPin,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NotoraPrimary,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
            )
        )

        // Favorite Filter
        FilterChip(
            selected = filter.isFavoriteOnly,
            onClick = rememberDebouncedClick { onFilterChange(filter.copy(isFavoriteOnly = !filter.isFavoriteOnly)) },
            label = { Text(stringResource(R.string.home_filter_favorites), style = AppTypography.labelMedium) },
            leadingIcon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NotoraRose,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
            )
        )

        // Category Filters
        categories.forEach { category ->
            val isSelected = filter.categoryId == category.id
            FilterChip(
                selected = isSelected,
                onClick = rememberDebouncedClick {
                    val nextCat = if (isSelected) null else category.id
                    onFilterChange(filter.copy(categoryId = nextCat))
                },
                label = { Text(category.name, style = AppTypography.labelMedium) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NotoraCyan,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }

        // Tag Filters
        tags.forEach { tag ->
            val isSelected = filter.tagId == tag.id
            FilterChip(
                selected = isSelected,
                onClick = rememberDebouncedClick {
                    val nextTag = if (isSelected) null else tag.id
                    onFilterChange(filter.copy(tagId = nextTag))
                },
                label = { Text("#${tag.name}", style = AppTypography.labelMedium) },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NotoraPrimary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}
