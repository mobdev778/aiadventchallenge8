package com.github.mobdev778.aiadventchallenge.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState

@Composable
fun TaskStateIndicator(
    state: TaskState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0x1AFFFFFF),
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dot(
                color = Color(0xFF7c4eba),
                enabled = state == TaskState.Planning,
            )
            Dot(
                color = Color(0xFF4daced),
                enabled = state == TaskState.Execution,
            )
            Dot(
                color = Color(0xFFf5a551),
                enabled = state == TaskState.Validation,
            )
            Dot(
                color = Color(0xFF70ee7d),
                enabled = state == TaskState.Done,
            )
        }
    }
}

@Composable
private fun Dot(
    color: Color,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .alpha(if (enabled) 1f else 0.3f)
            .background(color = color, shape = CircleShape)
    )
}
