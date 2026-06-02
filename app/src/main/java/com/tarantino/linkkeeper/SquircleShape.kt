package com.tarantino.linkkeeper

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.compose.ui.graphics.asComposePath
import androidx.graphics.shapes.toPath

fun SquircleShape(cornerRadius: Dp = 28.dp): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radiusStr = with(density) { cornerRadius.toPx() }
        
        val vertices = floatArrayOf(
            0f, 0f,
            size.width, 0f,
            size.width, size.height,
            0f, size.height
        )

        val polygon = RoundedPolygon(
            vertices = vertices,
            rounding = CornerRounding(radiusStr, smoothing = 0.6f)
        )
        
        val path = polygon.toPath()
        return Outline.Generic(path.asComposePath())
    }
}

fun SquircleShapePartial(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    bottomEnd: Dp = 0.dp
): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val ts = with(density) { topStart.toPx() }
        val te = with(density) { topEnd.toPx() }
        val bs = with(density) { bottomStart.toPx() }
        val be = with(density) { bottomEnd.toPx() }
        
        val roundings = listOf(
            CornerRounding(te, smoothing = if (te > 0) 0.6f else 0f),
            CornerRounding(be, smoothing = if (be > 0) 0.6f else 0f),
            CornerRounding(bs, smoothing = if (bs > 0) 0.6f else 0f),
            CornerRounding(ts, smoothing = if (ts > 0) 0.6f else 0f)
        )

        val vertices = floatArrayOf(
            0f, 0f,
            size.width, 0f,
            size.width, size.height,
            0f, size.height
        )

        val polygon = RoundedPolygon(
            vertices = vertices,
            perVertexRounding = roundings
        )
        return Outline.Generic(polygon.toPath().asComposePath())
    }
}
