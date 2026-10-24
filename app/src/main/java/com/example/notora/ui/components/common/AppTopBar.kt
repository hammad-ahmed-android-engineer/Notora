package com.example.notora.ui.components.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppTypography

/**
 * Standardized TopAppBar adhering to Notora's AppTypography and color schemes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = onNavigateBack?.let { callback ->
        {
            val debouncedBack = rememberDebouncedClick { callback() }
            IconButton(onClick = debouncedBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = AppTypography.toolbarTitle
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = colors
    )
}
