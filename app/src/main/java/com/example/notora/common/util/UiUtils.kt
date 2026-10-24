package com.example.notora.common.util

import android.content.res.Configuration
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

/**
 * Common UI and configuration helper utilities including debounced click extensions.
 */
object UiUtils {

    /**
     * Checks if current device configuration is landscape.
     */
    fun isLandscape(configuration: Configuration): Boolean {
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * Helper to compute responsive column count for grids.
     */
    fun getGridColumnCount(isLandscape: Boolean, portraitColumns: Int = 2, landscapeColumns: Int = 3): Int {
        return if (isLandscape) landscapeColumns else portraitColumns
    }

    fun getGridColumnCount(configuration: Configuration, portraitColumns: Int = 2, landscapeColumns: Int = 3): Int {
        return getGridColumnCount(isLandscape(configuration), portraitColumns, landscapeColumns)
    }
}

/**
 * Compose extension to handle click events with a 500ms debounce guard interval.
 * Prevents rapid accidental double-taps and duplicate triggers across all UI components.
 */
fun Modifier.clickableDebounced(
    debounceInterval: Long = 500L,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    indication: Indication? = null,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val currentInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        indication = indication,
        interactionSource = currentInteractionSource
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceInterval) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

/**
 * Composable helper that returns a debounced lambda with a 500ms guard interval.
 * Suitable for standard Material3 buttons like [IconButton], [FloatingActionButton],
 * [Button], [FilterChip], and [DropdownMenuItem].
 */
@Composable
fun rememberDebouncedClick(
    debounceInterval: Long = 500L,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return remember(onClick, debounceInterval) {
        {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= debounceInterval) {
                lastClickTime = currentTime
                onClick()
            }
        }
    }
}
