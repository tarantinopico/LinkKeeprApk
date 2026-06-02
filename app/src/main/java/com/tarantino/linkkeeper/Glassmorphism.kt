package com.tarantino.linkkeeper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glassSurface(darkMode: Boolean, shape: Shape, blurRadius: Dp = 20.dp): Modifier = composed {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (darkMode) 0.10f else 0.72f),
            Color.White.copy(alpha = if (darkMode) 0.05f else 0.55f)
        )
    )

    this
        .clip(shape)
        .background(gradientBrush)
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = if (darkMode) 0.10f else 0.30f),
            shape = shape
        )
        .border(
            width = 1.dp,
            color = Color.Black.copy(alpha = 0.04f),
            shape = shape
        )
}
