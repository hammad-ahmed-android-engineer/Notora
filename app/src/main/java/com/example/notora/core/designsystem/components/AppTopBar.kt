package com.example.notora.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.example.notora.R
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.utils.rememberDebouncedClick

/**
 * Standardized TopAppBar adhering to Notora's AppTypography and color schemes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String = "",
    titleContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = onNavigateBack?.let { callback ->
        {
            val debouncedBack = rememberDebouncedClick { callback() }
            AppIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = debouncedBack,
                contentDescription = stringResource(R.string.action_back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    titleStyle: TextStyle = AppTypography.toolbarTitle,
    centerTitle: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )
) {
    val resolvedTitleContent = titleContent ?: @Composable {
        AppText(
            text = title,
            style = titleStyle
        )
    }

    if (centerTitle) {
        CenterAlignedTopAppBar(
            title = resolvedTitleContent,
            modifier = modifier,
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = colors
        )
    } else {
        TopAppBar(
            title = resolvedTitleContent,
            modifier = modifier,
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = colors
        )
    }
}
