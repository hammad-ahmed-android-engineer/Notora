package com.example.notora.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary

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
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.EventNote,
                    contentDescription = null,
                    tint = NotoraPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = formattedDate,
                    style = AppTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            TextButton(
                onClick = rememberDebouncedClick { onChangeDateClick() },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.calendar_change_date),
                    style = AppTypography.buttonText,
                    color = NotoraPrimary
                )
            }
        }
    }
}
