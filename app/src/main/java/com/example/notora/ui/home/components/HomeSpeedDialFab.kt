package com.example.notora.ui.home.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraCyan
import com.example.notora.ui.theme.NotoraPrimary

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
        modifier = modifier.padding(end = 4.dp, bottom = 4.dp)
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
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckBox, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.home_new_checklist), style = AppTypography.buttonText)
                    }
                }

                FloatingActionButton(
                    onClick = rememberDebouncedClick {
                        onToggleExpand()
                        onCreateStandardNote()
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = NotoraPrimary,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.home_new_note), style = AppTypography.buttonText)
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
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier.size(58.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.action_create),
                modifier = Modifier
                    .size(28.dp)
                    .rotate(fabRotation)
            )
        }
    }
}
