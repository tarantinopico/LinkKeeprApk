package com.tarantino.linkkeeper

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkCard(
    link: SavedLink,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onToggleRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val view = LocalView.current
    
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggleRead()
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { with(density) { 120.dp.toPx() } }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        modifier = modifier.clip(RoundedCornerShape(28.dp)),
        backgroundContent = {
            val direction = dismissState.dismissDirection
            if (direction != null) {
                val color = if (direction == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9)
                val icon = if (direction == SwipeToDismissBoxValue.EndToStart) Icons.Default.Delete else if (link.isRead) Icons.Default.Close else Icons.Default.Check
                val tint = if (direction == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                val alignment = if (direction == SwipeToDismissBoxValue.EndToStart) Alignment.CenterEnd else Alignment.CenterStart
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 24.dp),
                    contentAlignment = alignment
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = tint)
                }
            }
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (link.isRead) 0.6f else 1f)
                    .hairlineBorder(RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column {
                    if (!link.thumbnailUri.isNullOrBlank()) {
                        AsyncImage(
                            model = link.thumbnailUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val initialLetter = runCatching { Uri.parse(link.url).host?.firstOrNull()?.uppercase() }.getOrNull() ?: "L"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initialLetter,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = link.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = link.description,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        if (link.userNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = link.userNote,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                val host = runCatching { Uri.parse(link.url).host }.getOrNull()
                                if (!host.isNullOrBlank()) {
                                    AsyncImage(
                                        model = "https://www.google.com/s2/favicons?domain=$host&sz=64",
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = host ?: "unknown",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = onCopy,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Link",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onOpen,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = "Open Link",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
