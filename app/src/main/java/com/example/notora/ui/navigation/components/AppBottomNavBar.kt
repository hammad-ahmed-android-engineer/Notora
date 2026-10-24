package com.example.notora.ui.navigation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.clickableDebounced
import com.example.notora.ui.navigation.Screen
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary
import com.example.notora.common.util.rememberDebouncedClick

/**
 * Custom cradle shape creating a smooth concave dip in the top-center edge
 * to nest the floating center Floating Action Button seamlessly.
 */
class CradleBottomBarShape(
    private val cornerRadius: Dp = 20.dp,
    private val cradleRadius: Dp = 38.dp,
    private val cradleDepth: Dp = 18.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val cRadius = with(density) { cornerRadius.toPx() }
            val fabRadius = with(density) { cradleRadius.toPx() }
            val depth = with(density) { cradleDepth.toPx() }
            val centerX = size.width / 2f

            moveTo(0f, cRadius)
            quadraticTo(0f, 0f, cRadius, 0f)
            lineTo(centerX - fabRadius - 10f, 0f)

            // Smooth curved cradle notch around floating FAB
            cubicTo(
                centerX - fabRadius + 4f, 0f,
                centerX - fabRadius / 2f, depth,
                centerX, depth
            )
            cubicTo(
                centerX + fabRadius / 2f, depth,
                centerX + fabRadius - 4f, 0f,
                centerX + fabRadius + 10f, 0f
            )

            lineTo(size.width - cRadius, 0f)
            quadraticTo(size.width, 0f, size.width, cRadius)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Modern cradled bottom navigation bar featuring:
 * - 4 primary tab destinations (Notes, Folders, Calendar, Settings)
 * - Prominent center Floating Action Button resting gracefully inside a concave cradle
 * - Solid docked surface extending through navigation bar insets to the screen edge
 */
@Composable
fun AppBottomNavBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onCreateNote: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Cradled Bottom Bar Surface
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CradleBottomBarShape(
                cornerRadius = 20.dp,
                cradleRadius = 38.dp,
                cradleDepth = 18.dp
            ),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item 1: Notes
                    NavBarTabItem(
                        label = stringResource(R.string.nav_bottom_notes),
                        icon = Icons.AutoMirrored.Filled.Notes,
                        selected = currentRoute == Screen.HOME,
                        onClick = onNavigateToHome,
                        modifier = Modifier.weight(1f)
                    )

                    // Item 2: Folders
                    NavBarTabItem(
                        label = stringResource(R.string.nav_bottom_folders),
                        icon = Icons.Default.Folder,
                        selected = currentRoute == Screen.CATEGORIES,
                        onClick = onNavigateToCategories,
                        modifier = Modifier.weight(1f)
                    )

                    // Center Spacer matching the FAB cradle width
                    Box(modifier = Modifier.weight(1.2f))

                    // Item 3: Calendar
                    NavBarTabItem(
                        label = stringResource(R.string.nav_bottom_calendar),
                        icon = Icons.Default.CalendarMonth,
                        selected = currentRoute == Screen.CALENDAR,
                        onClick = onNavigateToCalendar,
                        modifier = Modifier.weight(1f)
                    )

                    // Item 4: Settings
                    NavBarTabItem(
                        label = stringResource(R.string.nav_bottom_settings),
                        icon = Icons.Default.Settings,
                        selected = currentRoute == Screen.SETTINGS,
                        onClick = onNavigateToSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Floating Center Action Button nested gracefully over the cradle
        val centerAction = onCreateNote ?: onNavigateToHome
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp),
            contentAlignment = Alignment.Center
        ) {
            // Subtle glowing halo ring around floating FAB
            Surface(
                shape = CircleShape,
                color = NotoraPrimary.copy(alpha = 0.15f),
                modifier = Modifier.size(64.dp)
            ) {}

            FloatingActionButton(
                onClick = centerAction,
                shape = CircleShape,
                containerColor = NotoraPrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                ),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.nav_create_note),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun NavBarTabItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tab_icon_scale"
    )

    val activeColor = NotoraPrimary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickableDebounced(
                debounceInterval = 500L,
                interactionSource = interactionSource,
                indication = null,
                onClick = rememberDebouncedClick { onClick() }
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .scale(iconScale),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Surface(
                    color = activeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.matchParentSize()
                ) {}
            }
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = label,
            style = AppTypography.caption,
            color = if (selected) activeColor else inactiveColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
