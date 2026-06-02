package com.tarantino.linkkeeper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GroupCard(
    group: GroupWithCount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (group.group.iconName) {
        "Folder" -> Icons.Default.Home
        "Movie" -> Icons.Default.Face
        "Restaurant" -> Icons.Default.Favorite
        "Article" -> Icons.Default.Email
        "Code" -> Icons.Default.Home
        "Lock" -> Icons.Default.Lock
        else -> Icons.Default.Home
    }
    
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color(android.graphics.Color.parseColor(group.group.colorHex)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .width(150.dp)
            .height(140.dp)
            .hairlineBorder(RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            
            Column {
                Text(
                    text = group.group.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${group.count} links",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
