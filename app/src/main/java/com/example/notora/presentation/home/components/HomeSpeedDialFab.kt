package com.example.notora.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.notora.R
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraCyan
import com.example.notora.core.designsystem.NotoraPrimary

/**
 * Expandable Floating Action Button speed dial for creating standard notes or checklists.
 */
@Composable
fun HomeSpeedDialFab(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCreateStandardNote: () -> Unit,
    onCreateChecklistNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fabRotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_rotation"
    )

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier.padding(end = AppDimensions.paddingMicro, bottom = AppDimensions.paddingMicro)
    ) {
        // Animated speed dial options
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = tween(150)) + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut(animationSpec = tween(150)) + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = rememberDebouncedClick {
                        onToggleExpand()
                        onCreateChecklistNote()
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = NotoraCyan,
                    shape = RoundedCornerShape(AppDimensions.cornerLarge),
                    elevation = FloatingActionButtonDefaults.elevation(AppDimensions.elevations.medium),
                    modifier = Modifier.padding(bottom = AppDimensions.paddingMedium)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppDimensions.paddingDefault, vertical = AppDimensions.paddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckBox, contentDescription = null, modifier = Modifier.size(AppDimensions.iconMedium))
                        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                        AppText(stringResource(R.string.home_new_checklist), style = AppTypography.buttonText)
                    }
                }

                FloatingActionButton(
                    onClick = rememberDebouncedClick {
                        onToggleExpand()
                        onCreateStandardNote()
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = NotoraPrimary,
                    shape = RoundedCornerShape(AppDimensions.cornerLarge),
                    elevation = FloatingActionButtonDefaults.elevation(AppDimensions.elevations.medium),
                    modifier = Modifier.padding(bottom = AppDimensions.paddingDefault)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppDimensions.paddingDefault, vertical = AppDimensions.paddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = null, modifier = Modifier.size(AppDimensions.iconMedium))
                        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                        AppText(stringResource(R.string.home_new_note), style = AppTypography.buttonText)
                    }
                }
            }
        }

        // Main Rotating FAB
        FloatingActionButton(
            onClick = rememberDebouncedClick { onToggleExpand() },
            shape = CircleShape,
            containerColor = NotoraPrimary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(AppDimensions.elevations.high),
            modifier = Modifier.size(AppDimensions.components.fab)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.action_create),
                modifier = Modifier
                    .size(AppDimensions.icons.extraLarge)
                    .rotate(fabRotation)
            )
        }
    }
}
