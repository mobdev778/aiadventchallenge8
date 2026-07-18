package com.github.mobdev778.aiadventchallenge.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import org.jetbrains.jewel.ui.component.Text

@Suppress("MagicNumber")
private val INDICATOR_BACKGROUND_COLOR = Color(0xFF3F3F3F)
@Suppress("MagicNumber")
private val AUTO_PLAY_ON_COLOR = Color(0xFF70ee7d)
@Suppress("MagicNumber")
private val AUTO_PLAY_OFF_COLOR = Color(0xFF777777)

/**
 * Цвета, соответствующие набору состояний [TaskState] для визуализации прогресса.
 *
 * Каждый цвет в списке (по индексу) сопоставлен с элементом перечисления [TaskState] в порядке объявления:
 * [TaskState.Planning] → [TaskState.Execution] → [TaskState.Validation] → [TaskState.PrintResult] → [TaskState.Result].
 * Используется компонентом [TaskStateIndicator] для выделения активной стадии среди точек-индикаторов.
 */
@Suppress("MagicNumber")
val taskStateColors = listOf(
    Color(0xFF7c4eba), Color(0xFF4daced), Color(0xFFf5a551), Color(0xFFF485F8), Color(0xFF70ee7d),
)

/**
 * Индикатор состояния задачи с возможностью отображения статуса автоматического режима.
 *
 * Компонент визуализирует текущую стадию жизненного цикла задачи ([TaskState]) в виде линейной последовательности
 * цветных точек ([Dot]), где активная стадия выделяется полной непрозрачностью, а неактивные — полупрозрачны.
 * При наличии информации об автоматическом воспроизведении отображается соответствующий блок
 * с текстом "Auto Play: ON/OFF".
 *
 * @param autoPlay Статус автоматического воспроизведения:
 * - `true` – автоматический режим включён (надпись "ON" зелёным),
 * - `false` – автоматический режим выключен (надпись "OFF" серым),
 * - `null` – блок авто‑воспроизведения не отображается.
 * @param state Текущее состояние задачи из перечисления [TaskState]. Определяет, какая точка будет активной.
 * @param modifier Модификатор для корневого контейнера [Row]. По умолчанию [Modifier].
 */
@Composable
fun TaskStateIndicator(
    autoPlay: Boolean?,
    state: TaskState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(
                color = INDICATOR_BACKGROUND_COLOR,
                shape = RoundedCornerShape(16.dp),
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (autoPlay != null) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Auto Play:",
                    color = Color.White,
                )
                if (autoPlay) {
                    Text(
                        text = "ON",
                        color = AUTO_PLAY_ON_COLOR
                    )
                } else {
                    Text(
                        text = "OFF",
                        color = AUTO_PLAY_OFF_COLOR
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(8.dp)
                .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(12.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TaskState.entries.forEachIndexed { index, iState ->
                if (index != 0) { Text("→", color = Color.Gray) }
                Dot(
                    color = taskStateColors[index],
                    enabled = state == iState,
                )
            }
        }
    }
}
