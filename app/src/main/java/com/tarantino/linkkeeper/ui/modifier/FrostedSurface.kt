package com.tarantino.linkkeeper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.frostedSurface(darkMode: Boolean, shape: Shape, borderWidth: Dp = 0.5.dp): Modifier = composed {
    val gradientColors = listOf(
        Color.White.copy(alpha = if (darkMode) 0.10f else 0.82f),
        Color.White.copy(alpha = if (darkMode) 0.05f else 0.65f),
        Color.White.copy(alpha = if (darkMode) 0.02f else 0.45f)
    )
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = if (darkMode) 0.10f else 0.08f)

    this
        .background(Brush.verticalGradient(colors = gradientColors), shape)
        .border(borderWidth, borderColor, shape)
        .clip(shape) // Clip to avoid highlight leaking outside corners
}

@Composable
fun FrostedHighlight(darkMode: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = if (darkMode) 0.08f else 0.20f))
    )
}
