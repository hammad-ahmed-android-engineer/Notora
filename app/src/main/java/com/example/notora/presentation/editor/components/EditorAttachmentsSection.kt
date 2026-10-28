package com.example.notora.presentation.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.notora.R
import com.example.notora.core.designsystem.AppDimensions
import com.example.notora.core.designsystem.AppTypography
import com.example.notora.core.designsystem.NotoraPrimary
import com.example.notora.core.designsystem.components.AppIconButton
import com.example.notora.core.designsystem.components.AppText
import com.example.notora.domain.model.Attachment

/**
 * Renders thumbnail attachments row with remove actions and support for images and document files.
 */
@Composable
fun EditorAttachmentsSection(
    attachments: List<Attachment>,
    onRemoveAttachment: (Attachment) -> Unit,
    modifier: Modifier = Modifier
) {
    if (attachments.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        AppText(
            text = "${stringResource(R.string.editor_attachments_title)} (${attachments.size})",
            style = AppTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NotoraPrimary
        )
        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)) {
            items(attachments) { attachment ->
                Box(
                    modifier = Modifier
                        .size(AppDimensions.components.emptyStateIconContainer * 1.5f)
                        .clip(RoundedCornerShape(AppDimensions.cornerLarge))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (attachment.mimeType.startsWith("image/")) {
                        AsyncImage(
                            model = attachment.uri,
                            contentDescription = attachment.displayName,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppDimensions.paddingSmall),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = NotoraPrimary
                            )
                            Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                            AppText(
                                text = attachment.displayName,
                                style = AppTypography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }

                    AppIconButton(
                        icon = Icons.Default.Close,
                        onClick = { onRemoveAttachment(attachment) },
                        contentDescription = stringResource(R.string.editor_delete_attachment),
                        size = AppDimensions.cornerPill,
                        iconSize = AppDimensions.iconSmall,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}
