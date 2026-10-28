package com.example.notora.core.designsystem.components.toolbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.notora.core.designsystem.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.utils.rememberDebouncedClick
import com.example.notora.core.designsystem.components.AppButton
import com.example.notora.core.designsystem.components.AppTextButton
import com.example.notora.core.designsystem.components.AppTextField

/**
 * Animated subpanel for entering and inserting a hyperlink into rich text content.
 */
@Composable
fun LinkSubPanel(
    visible: Boolean,
    linkUrl: String,
    onLinkUrlChange: (String) -> Unit,
    onInsertLink: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.paddingLarge, vertical = AppDimensions.paddingMedium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = stringResource(R.string.richtext_insert_web_link),
                        style = AppTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = NotoraPrimary
                    )
                    IconButton(
                        onClick = rememberDebouncedClick { onClose() },
                        modifier = Modifier.size(AppDimensions.iconLarge)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.action_close),
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                AppTextField(
                    value = linkUrl,
                    onValueChange = onLinkUrlChange,
                    placeholder = stringResource(R.string.richtext_link_url_hint),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppDimensions.cornerMedium)
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    AppTextButton(
                        text = stringResource(R.string.action_cancel),
                        onClick = onClose
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                    AppButton(
                        text = stringResource(R.string.richtext_insert_action),
                        onClick = onInsertLink,
                        shape = RoundedCornerShape(AppDimensions.cornerMedium),
                        colors = ButtonDefaults.buttonColors(containerColor = NotoraPrimary)
                    )
                }
            }
        }
    }
}
