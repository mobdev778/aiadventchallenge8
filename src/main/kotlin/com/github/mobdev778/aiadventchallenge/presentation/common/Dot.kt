package com.github.mobdev778.aiadventchallenge.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private const val ENABLED_ALPHA = 1f
private const val DISABLED_ALPHA = 0.2f

@Composable
fun Dot(
    color: Color,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .alpha(if (enabled) ENABLED_ALPHA else DISABLED_ALPHA)
            .background(color = color, shape = CircleShape)
    )
}
