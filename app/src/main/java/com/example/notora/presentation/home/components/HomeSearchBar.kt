package com.example.notora.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppIconButton
import com.example.notora.core.designsystem.components.AppText

/**
 * Modern floating pill search bar with menu drawer trigger, full-width search input,
 * query clearing, and grid/list layout mode toggling.
 */
@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isStaggeredGrid: Boolean,
    onToggleLayout: () -> Unit,
    onOpenDrawer: () -> Unit,
    showDrawerButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.paddingLarge, vertical = AppDimensions.paddingSmall)
            .height(AppDimensions.searchBarHeight),
        shape = RoundedCornerShape(AppDimensions.cornerPill),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = AppDimensions.elevations.low,
        shadowElevation = AppDimensions.elevations.low
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppDimensions.paddingSmall)
        ) {
            if (showDrawerButton) {
                AppIconButton(
                    icon = Icons.Default.Menu,
                    onClick = onOpenDrawer,
                    contentDescription = stringResource(R.string.home_open_drawer),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = {
                    AppText(
                        text = stringResource(R.string.home_search_placeholder),
                        style = AppTypography.inputHintText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                textStyle = AppTypography.inputFieldText.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = NotoraPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppDimensions.paddingSmall)
            )

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AppIconButton(
                    icon = Icons.Default.Clear,
                    onClick = { onQueryChange("") },
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    iconSize = AppDimensions.iconMedium
                )
            }

            AppIconButton(
                icon = if (isStaggeredGrid) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                onClick = onToggleLayout,
                contentDescription = stringResource(R.string.home_toggle_layout),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
