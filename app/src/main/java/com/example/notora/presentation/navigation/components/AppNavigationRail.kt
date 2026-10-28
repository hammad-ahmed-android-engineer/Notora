package com.example.notora.presentation.navigation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.presentation.navigation.Screen
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary

/**
 * Standardized Material 3 navigation rail providing seamless navigation between primary screens
 * in landscape orientation.
 */
@Composable
fun AppNavigationRail(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)),
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Box(
                modifier = Modifier
                    .padding(vertical = AppDimensions.paddingMedium)
                    .size(AppDimensions.searchBarHeight)
                    .clip(CircleShape)
                    .background(NotoraPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = stringResource(R.string.app_brand_title),
                    modifier = Modifier.size(AppDimensions.icons.extraLarge)
                )
            }
        }
    ) {
        val selectedColors = NavigationRailItemDefaults.colors(
            selectedIconColor = NotoraPrimary,
            selectedTextColor = NotoraPrimary,
            indicatorColor = NotoraPrimary.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

        NavigationRailItem(
            selected = currentRoute == Screen.HOME,
            onClick = rememberDebouncedClick { onNavigateToHome() },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = stringResource(R.string.nav_notes)
                )
            },
            label = { AppText(stringResource(R.string.nav_notes), style = AppTypography.caption) },
            colors = selectedColors
        )

        NavigationRailItem(
            selected = currentRoute == Screen.CATEGORIES,
            onClick = rememberDebouncedClick { onNavigateToCategories() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = stringResource(R.string.nav_categories)
                )
            },
            label = { AppText(stringResource(R.string.nav_categories), style = AppTypography.caption) },
            colors = selectedColors
        )

        NavigationRailItem(
            selected = currentRoute == Screen.CALENDAR,
            onClick = rememberDebouncedClick { onNavigateToCalendar() },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.nav_calendar)
                )
            },
            label = { AppText(stringResource(R.string.nav_calendar), style = AppTypography.caption) },
            colors = selectedColors
        )

        NavigationRailItem(
            selected = currentRoute == Screen.ARCHIVE,
            onClick = rememberDebouncedClick { onNavigateToArchive() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Archive,
                    contentDescription = stringResource(R.string.nav_archive)
                )
            },
            label = { AppText(stringResource(R.string.nav_archive), style = AppTypography.caption) },
            colors = selectedColors
        )

        Spacer(modifier = Modifier.weight(1f))

        NavigationRailItem(
            selected = currentRoute == Screen.SETTINGS,
            onClick = rememberDebouncedClick { onNavigateToSettings() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.nav_settings)
                )
            },
            label = { AppText(stringResource(R.string.nav_settings), style = AppTypography.caption) },
            colors = selectedColors
        )
    }
}
