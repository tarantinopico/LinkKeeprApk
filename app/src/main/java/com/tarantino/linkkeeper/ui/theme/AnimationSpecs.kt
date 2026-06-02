package com.tarantino.linkkeeper

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

val iosSpringOffset = spring<IntOffset>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

val iosFade = tween<Float>(durationMillis = 300)
