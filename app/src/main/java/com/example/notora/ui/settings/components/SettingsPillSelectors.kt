package com.example.notora.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notora.common.util.clickableDebounced
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary

/**
 * Full-width theme selector pill row for switching between System, Light, and Dark themes.
 */
@Composable
fun ThemePillSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("SYSTEM", "LIGHT", "DARK")
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedTheme == option
                val bgColor = if (isSelected) NotoraPrimary else Color.Transparent
                val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(11.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .clickableDebounced(debounceInterval = 500L) {  onThemeSelected(option) }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (option) {
                                "SYSTEM" -> Icons.Default.PhoneAndroid
                                "LIGHT" -> Icons.Default.LightMode
                                else -> Icons.Default.DarkMode
                            },
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (option) {
                                "SYSTEM" -> "Auto"
                                "LIGHT" -> "Light"
                                else -> "Dark"
                            },
                            style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full-width layout selector pill row for switching between Grid View and List View.
 */
@Composable
fun LayoutPillSelector(
    selectedLayout: String,
    onLayoutSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("STAGGERED_GRID", "LIST")
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedLayout == option
                val bgColor = if (isSelected) NotoraPrimary else Color.Transparent
                val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(11.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .clickableDebounced(debounceInterval = 500L) {  onLayoutSelected(option) }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (option == "STAGGERED_GRID") Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (option == "STAGGERED_GRID") "Grid View" else "List View",
                            style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
