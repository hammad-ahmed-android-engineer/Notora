package com.example.notora.presentation.calendar.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppCard
import com.example.notora.core.designsystem.components.AppText
import com.example.notora.core.designsystem.components.AppTextButton

/**
 * Clean date banner header for the Calendar screen displaying the formatted selected date
 * and an action button to open the date picker.
 */
@Composable
fun CalendarDateHeader(
    formattedDate: String,
    onChangeDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.paddingLarge, vertical = AppDimensions.paddingSmall),
        onClick = onChangeDateClick,
        shape = RoundedCornerShape(AppDimensions.cornerLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            AppDimensions.borders.thin,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(AppDimensions.paddingDefault)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.EventNote,
                    contentDescription = null,
                    tint = NotoraPrimary,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
                AppText(
                    text = formattedDate.ifBlank { "Select Date" },
                    style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            AppTextButton(
                text = stringResource(R.string.calendar_change_date),
                onClick = onChangeDateClick,
                color = NotoraPrimary
            )
        }
    }
}
