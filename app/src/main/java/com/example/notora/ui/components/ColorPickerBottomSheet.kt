package com.example.notora.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.clickableDebounced
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NoteColorPalette
import com.example.notora.ui.theme.NotoraPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ColorPickerBottomSheet(
    selectedColorHex: String,
    onColorSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val currentColor = NoteColorPalette.colors.firstOrNull { it.hex.equals(selectedColorHex, ignoreCase = true) }
        ?: NoteColorPalette.Default

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.color_picker_palette_title),
                    style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                    color = NotoraPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = currentColor.name,
                        style = AppTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = NotoraPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                NoteColorPalette.colors.forEach { noteColor ->
                    val isSelected = noteColor.hex.equals(selectedColorHex, ignoreCase = true)
                    val colorSample = NoteColorPalette.getBackgroundColor(noteColor.hex)

                    val swatchScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "swatch_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .scale(swatchScale)
                            .clip(CircleShape)
                            .background(colorSample)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) NotoraPrimary else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                            .clickableDebounced(debounceInterval = 500L) {
                                onColorSelected(noteColor.hex)
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.color_picker_selected_desc),
                                tint = NotoraPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
