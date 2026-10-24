package com.example.notora.ui.components.toolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.NotoraPrimary

/**
 * Capsule background container that groups related formatting tools together.
 */
@Composable
fun ToolGroupContainer(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp)
        ) {
            content()
        }
    }
}

/**
 * Reusable animated toggle icon button for rich text formatting controls.
 */
@Composable
fun FormattingIconButton(
    isActive: Boolean,
    onClick: () -> Unit,
    label: String,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btn_scale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isActive) NotoraPrimary else Color.Transparent,
        label = "btn_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface,
        label = "btn_fg"
    )

    IconButton(
        onClick = rememberDebouncedClick { onClick() },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = bgColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
    ) {
        content()
    }
}

/**
 * Thin subtle vertical divider separating toolbar action groups.
 */
@Composable
fun ToolbarDivider() {
    Surface(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .size(width = 1.dp, height = 22.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    ) {}
}
