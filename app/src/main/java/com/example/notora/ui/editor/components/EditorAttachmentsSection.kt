package com.example.notora.ui.editor.components

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.notora.R
import com.example.notora.common.util.rememberDebouncedClick
import com.example.notora.domain.model.Attachment
import com.example.notora.ui.theme.AppTypography
import com.example.notora.ui.theme.NotoraPrimary

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
        Text(
            text = "${stringResource(R.string.editor_attachments_title)} (${attachments.size})",
            style = AppTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NotoraPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(attachments) { attachment ->
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(14.dp))
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
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = NotoraPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = attachment.displayName,
                                style = AppTypography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = rememberDebouncedClick { onRemoveAttachment(attachment) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.editor_delete_attachment),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
