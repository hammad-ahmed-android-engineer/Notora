package com.example.notora.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Idiomatic Compose extension to resolve scalable dp (SDP).
 * Adapts seamlessly across phone, foldable, and tablet screen widths.
 */
val Int.sdp: Dp
    @Composable
    get() {
        val context = LocalContext.current
        val id = remember(this) {
            val pkg = context.packageName
            val localId = context.resources.getIdentifier("_" + this + "sdp", "dimen", pkg)
            if (localId != 0) localId else context.resources.getIdentifier("_" + this + "sdp", "dimen", "com.intuit.sdp")
        }
        return if (id != 0) dimensionResource(id) else this.dp
    }

val Double.sdp: Dp
    @Composable
    get() = this.toInt().sdp

val Float.sdp: Dp
    @Composable
    get() = this.toInt().sdp

/**
 * Idiomatic Compose extension to resolve scalable sp (SSP).
 * Respects accessibility font scaling while maintaining proportionate sizing.
 */
val Int.ssp: TextUnit
    @Composable
    get() {
        val context = LocalContext.current
        val density = LocalDensity.current
        val id = remember(this) {
            val pkg = context.packageName
            val localId = context.resources.getIdentifier("_" + this + "ssp", "dimen", pkg)
            if (localId != 0) localId else context.resources.getIdentifier("_" + this + "ssp", "dimen", "com.intuit.ssp")
        }
        return if (id != 0) {
            val dpVal = dimensionResource(id)
            with(density) { dpVal.toSp() }
        } else {
            this.sp
        }
    }

val Double.ssp: TextUnit
    @Composable
    get() = this.toInt().ssp

val Float.ssp: TextUnit
    @Composable
    get() = this.toInt().ssp

/**
 * Centralized semantic dimensions system across Notora.
 * Consistently leverages scalable SDP units for responsive layouts.
 */
object AppDimensions {

    // Spacing & Padding
    val paddingZero: Dp @Composable get() = 0.sdp
    val paddingMicro: Dp @Composable get() = 2.sdp
    val paddingTiny: Dp @Composable get() = 4.sdp
    val paddingSmall: Dp @Composable get() = 8.sdp
    val paddingMedium: Dp @Composable get() = 16.sdp
    val paddingLarge: Dp @Composable get() = 24.sdp
    val paddingExtraLarge: Dp @Composable get() = 32.sdp

    // Corner Radii
    val cornerMicro: Dp @Composable get() = 4.sdp
    val cornerSmall: Dp @Composable get() = 8.sdp
    val cornerMedium: Dp @Composable get() = 12.sdp
    val cornerLarge: Dp @Composable get() = 16.sdp
    val cornerExtraLarge: Dp @Composable get() = 20.sdp
    val cornerPill: Dp @Composable get() = 28.sdp

    val radiusSmall: Dp @Composable get() = cornerSmall
    val radiusMedium: Dp @Composable get() = cornerMedium
    val radiusLarge: Dp @Composable get() = cornerLarge
    val radiusExtraLarge: Dp @Composable get() = cornerExtraLarge
    val radiusFull: Dp @Composable get() = cornerPill

    // Icon Sizes
    val iconTiny: Dp @Composable get() = 12.sdp
    val iconSmall: Dp @Composable get() = 16.sdp
    val iconMedium: Dp @Composable get() = 20.sdp
    val iconLarge: Dp @Composable get() = 24.sdp
    val iconExtraLarge: Dp @Composable get() = 36.sdp
    val iconHuge: Dp @Composable get() = 44.sdp

    // Component Dimensions
    val buttonHeight: Dp @Composable get() = 48.sdp
    val buttonSmallHeight: Dp @Composable get() = 36.sdp
    val searchFieldHeight: Dp @Composable get() = 48.sdp
    val searchBarHeight: Dp @Composable get() = 48.sdp
    val dialogPadding: Dp @Composable get() = paddingLarge
    val fabSize: Dp @Composable get() = 58.sdp
    val avatarSize: Dp @Composable get() = 42.sdp
    val emptyStateIconContainerSize: Dp @Composable get() = 72.sdp
    val colorPickerCircleSize: Dp @Composable get() = 42.sdp
    val attachmentThumbnailSize: Dp @Composable get() = 90.sdp
}
