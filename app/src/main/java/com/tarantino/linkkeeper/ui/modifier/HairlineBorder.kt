package com.tarantino.linkkeeper

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.hairlineBorder(shape: Shape, alpha: Float = 0.08f): Modifier = composed {
    this.border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = alpha), shape)
}
