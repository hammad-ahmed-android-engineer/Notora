package com.example.notora.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary

/**
 * Modern modal navigation drawer sheet containing branding, notebook statistics, and navigation destinations.
 */
@Composable
fun HomeDrawerContent(
    totalNotesCount: Int,
    categoriesCount: Int,
    onNavigateToCategories: () -> Unit,
    onNavigateToTags: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(AppDimensions.components.emptyStateIconContainer * 5),
        drawerShape = RoundedCornerShape(topEnd = AppDimensions.cornerPill, bottomEnd = AppDimensions.cornerPill),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = AppDimensions.paddingLarge, vertical = AppDimensions.cornerPill)
                .fillMaxHeight()
        ) {
            Surface(
                shape = RoundedCornerShape(AppDimensions.cornerLarge),
                color = NotoraPrimary.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppDimensions.paddingLarge)
            ) {
                Column(modifier = Modifier.padding(AppDimensions.paddingLarge)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_splash_logo),
                            contentDescription = stringResource(R.string.app_brand_title),
                            modifier = Modifier.size(AppDimensions.searchBarHeight)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingMedium))
                        Column {
                            AppText(
                                text = stringResource(R.string.app_brand_title),
                                style = AppTypography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = NotoraPrimary
                            )
                            AppText(
                                text = stringResource(R.string.app_tagline),
                                style = AppTypography.bodySmall,
                                color = NotoraPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(AppDimensions.cornerMedium)
                    ) {
                        AppText(
                            text = "$totalNotesCount Total Notes · $categoriesCount Folders",
                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingMicro),
                            color = NotoraPrimary
                        )
                    }
                }
            }

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_notes), style = AppTypography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                selected = true,
                onClick = rememberDebouncedClick { onCloseDrawer() },
                icon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = NotoraPrimary.copy(alpha = 0.15f),
                    selectedTextColor = NotoraPrimary,
                    selectedIconColor = NotoraPrimary
                )
            )

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_categories), style = AppTypography.bodyLarge) },
                selected = false,
                onClick = rememberDebouncedClick {
                    onCloseDrawer()
                    onNavigateToCategories()
                },
                icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            )

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_tags), style = AppTypography.bodyLarge) },
                selected = false,
                onClick = rememberDebouncedClick {
                    onCloseDrawer()
                    onNavigateToTags()
                },
                icon = { Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            )

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_calendar), style = AppTypography.bodyLarge) },
                selected = false,
                onClick = rememberDebouncedClick {
                    onCloseDrawer()
                    onNavigateToCalendar()
                },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            )

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_templates), style = AppTypography.bodyLarge) },
                selected = false,
                onClick = rememberDebouncedClick {
                    onCloseDrawer()
                    onNavigateToTemplates()
                },
                icon = { Icon(Icons.AutoMirrored.Filled.LibraryBooks, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            )

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_archive), style = AppTypography.bodyLarge) },
                selected = false,
                onClick = rememberDebouncedClick {
                    onCloseDrawer()
                    onNavigateToArchive()
                },
                icon = { Icon(Icons.Default.Archive, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            )

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_trash), style = AppTypography.bodyLarge) },
                selected = false,
                onClick = rememberDebouncedClick {
                    onCloseDrawer()
                    onNavigateToTrash()
                },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            )

            Spacer(modifier = Modifier.weight(1f))

            NavigationDrawerItem(
                label = { AppText(stringResource(R.string.nav_settings), style = AppTypography.bodyLarge) },
                selected = false,
                onClick = rememberDebouncedClick {
                    onCloseDrawer()
                    onNavigateToSettings()
                },
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            )
        }
    }
}
