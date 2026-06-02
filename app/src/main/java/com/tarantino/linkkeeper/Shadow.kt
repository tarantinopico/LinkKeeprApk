package com.tarantino.linkkeeper

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.iosShadow(shape: Shape, intensity: Float = 1f): Modifier {
    return this
        .shadow(
            elevation = 2.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.04f * intensity),
            spotColor = Color.Black.copy(alpha = 0.02f * intensity)
        )
        .padding(2.dp)
        .shadow(
            elevation = 8.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.03f * intensity),
            spotColor = Color.Black.copy(alpha = 0.04f * intensity)
        )
        .padding(4.dp)
        .shadow(
            elevation = 24.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.02f * intensity),
            spotColor = Color.Black.copy(alpha = 0.06f * intensity)
        )
}
