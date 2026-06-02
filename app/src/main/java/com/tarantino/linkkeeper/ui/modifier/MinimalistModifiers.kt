package com.tarantino.linkkeeper

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.hairlineBorder(shape: Shape): Modifier = composed {
    val hairlineColor = if (androidx.compose.foundation.isSystemInDarkTheme()) NordicDarkHairline else NordicLightHairline
    this.border(width = 0.5.dp, color = hairlineColor, shape = shape)
}

fun Modifier.glassPanel(darkMode: Boolean, shape: Shape, blurRadius: Dp = 20.dp): Modifier = composed {
    val overlayColorTop = if (darkMode) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.65f)
    val overlayColorBottom = if (darkMode) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.50f)
    
    val strokeColor = if (darkMode) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.15f)

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(overlayColorTop, overlayColorBottom)
    )

    this
        .clip(shape)
        .then(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.graphicsLayer {
                    renderEffect = RenderEffect
                        .createBlurEffect(
                            blurRadius.toPx(),
                            blurRadius.toPx(),
                            Shader.TileMode.CLAMP
                        )
                        .asComposeRenderEffect()
                }
            } else {
                Modifier
            }
        )
        .background(gradientBrush)
        .border(width = 1.dp, color = strokeColor, shape = shape)
}
