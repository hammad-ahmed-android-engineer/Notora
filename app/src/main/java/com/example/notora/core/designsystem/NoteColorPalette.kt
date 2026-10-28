package com.example.notora.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.notora.core.utils.AppUtils

data class NoteColor(
    val hex: String,
    val name: String,
    val lightBg: Color,
    val darkBg: Color
)

object NoteColorPalette {
    // Beautiful pastel paper colors for Light Mode & corresponding deep rich paper surfaces for Dark Mode
    val Default = NoteColor("#FFFFFF", "Pure White", Color(0xFFFFFFFF), Color(0xFF1E293B))
    val WarmVanilla = NoteColor("#FFF8E7", "Warm Vanilla", Color(0xFFFFF9EB), Color(0xFF2D2318))
    val Buttercup = NoteColor("#FFF9C4", "Buttercup Yellow", Color(0xFFFEF9C3), Color(0xFF2E2A12))
    val SoftPeach = NoteColor("#FFE0B2", "Soft Peach", Color(0xFFFFEDD5), Color(0xFF2E1E12))
    val CoralBlossom = NoteColor("#FFCDD2", "Coral Blossom", Color(0xFFFFE4E6), Color(0xFF2E1618))
    val SakuraPink = NoteColor("#F8BBD0", "Sakura Pink", Color(0xFFFCE7F3), Color(0xFF2E1624))
    val LilacMist = NoteColor("#E1BEE7", "Lilac Mist", Color(0xFFF3E8FF), Color(0xFF24162E))
    val Periwinkle = NoteColor("#D1C4E9", "Periwinkle", Color(0xFFEDE9FE), Color(0xFF1C162E))
    val SkyAzure = NoteColor("#BBDEFB", "Sky Azure", Color(0xFFE0F2FE), Color(0xFF12222E))
    val MatchaSage = NoteColor("#C8E6C9", "Matcha Sage", Color(0xFFDCFCE7), Color(0xFF122E1A))
    val MintFrost = NoteColor("#DCEDC8", "Mint Frost", Color(0xFFD1FAE5), Color(0xFF122E22))
    val SoftTeal = NoteColor("#B2DFDB", "Soft Teal", Color(0xFFCCFBF1), Color(0xFF122E2A))
    val CloudSlate = NoteColor("#F5F5F5", "Cloud Slate", Color(0xFFF1F5F9), Color(0xFF1E293B))

    val colors = listOf(
        Default, WarmVanilla, Buttercup, SoftPeach, CoralBlossom,
        SakuraPink, LilacMist, Periwinkle, SkyAzure, MatchaSage,
        MintFrost, SoftTeal, CloudSlate
    )

    @Composable
    fun getBackgroundColor(hex: String): Color {
        // Fetch the app's *actual* active theme from CompositionLocal, overriding the OS default
        val isDark = LocalThemeIsDark.current
        
        // 1. Direct hex lookup against our predefined palette keys
        val found = colors.firstOrNull { it.hex.equals(hex, ignoreCase = true) }
        if (found != null) {
            return if (isDark) found.darkBg else found.lightBg
        }

        // 2. Backward Compatibility: For existing notes with fixed color hexes, map them to the closest supported theme-aware color
        val parsed = AppUtils.parseHexColor(hex, fallback = Color.Unspecified)
        if (parsed != Color.Unspecified) {
            val closestMatch = colors.minByOrNull { colorDiff(parsed, it.lightBg) } ?: Default
            return if (isDark) closestMatch.darkBg else closestMatch.lightBg
        }

        // 3. Absolute Fallback
        return if (isDark) Default.darkBg else Default.lightBg
    }

    /**
     * Calculates color distance using simple RGB Euclidean distance to find the closest matching NoteColor.
     */
    private fun colorDiff(c1: Color, c2: Color): Float {
        val rDiff = c1.red - c2.red
        val gDiff = c1.green - c2.green
        val bDiff = c1.blue - c2.blue
        return (rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
    }
}
