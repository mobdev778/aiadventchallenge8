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

/**
 * Прозрачность для активного (включённого) состояния точки.
 */
private const val ENABLED_ALPHA = 1f

/**
 * Прозрачность для неактивного (выключенного) состояния точки.
 */
private const val DISABLED_ALPHA = 0.2f

/**
 * Простой компонент-индикатор в виде цветного круга.
 *
 * Используется для отображения статуса, индикации страниц (например, в [androidx.compose.material3.Pager])
 * или других визуальных маркеров, где требуется показать активное/неактивное состояние элемента.
 *
 * В активном состоянии точка отображается с полной непрозрачностью, в неактивном — полупрозрачной,
 * что позволяет легко выделять текущий элемент среди остальных.
 *
 * @param color Цвет заливки круга. Определяет визуальный стиль точки.
 * @param enabled Флаг активности. Если `true` — точка отображается с полной непрозрачностью,
 *   если `false` — с пониженной (20%).
 */
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
