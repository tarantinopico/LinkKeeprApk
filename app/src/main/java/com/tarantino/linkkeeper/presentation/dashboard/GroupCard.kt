package com.tarantino.linkkeeper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GroupCard(
    groupWithCount: GroupWithCount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupColor = runCatching { 
        Color(android.graphics.Color.parseColor(groupWithCount.group.colorHex)) 
    }.getOrDefault(MaterialTheme.colorScheme.primary)
    
    val icon = getIconForName(groupWithCount.group.iconName)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = groupColor,
        modifier = modifier
            .size(150.dp, 120.dp)
            .hairlineBorder(RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(androidx.compose.ui.Alignment.Center)
                )
            }
            
            Column {
                Text(
                    text = groupWithCount.group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "${groupWithCount.count} links",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

fun getIconForName(name: String): ImageVector {
    return when (name) {
        "work" -> Icons.Default.Work
        "home" -> Icons.Default.Home
        "star" -> Icons.Default.Star
        "favorite" -> Icons.Default.Favorite
        else -> Icons.Default.Folder
    }
}
