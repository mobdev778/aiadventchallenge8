package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.common.TaskStateIndicator
import com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.model.TaskContextScreenState
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text

/**
 * Основное содержимое экрана контекста задачи.
 *
 * Формирует прокручиваемую колонку с элементами:
 * - кнопка «Назад»;
 * - заголовок экрана [ScreenHeader] с текстом «Контекст задачи»;
 * - название задачи, её состояние (через [TaskStateLabel] и [TaskStateIndicator]),
 *   номер шага, утверждённый план в виде списка отмеченных/неотмеченных пунктов ([CheckedItem]),
 *   а также описание текущего выполняемого шага.
 *
 * Если контекст задачи отсутствует ([state.taskContext] == null), отображается
 * сообщение «Контекст не найден» и дальнейший рендеринг прекращается.
 *
 * @param state Состояние экрана, содержащее идентификаторы и объект контекста задачи.
 *              При наличии [TaskContext] строится детальное представление,
 *              иначе — заглушка.
 * @param onBack Колбэк, вызываемый при нажатии на кнопку «Назад».
 */
@Composable
fun TaskContextScreenContent(
    state: TaskContextScreenState,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = onBack) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Контекст задачи",
        )

        Spacer(modifier = Modifier.size(16.dp))

        val ctx = state.taskContext
        if (ctx == null) {
            Text("Контекст не найден")
            return
        }

        Text("Задача")
        Text(ctx.task)

        Spacer(modifier = Modifier.size(12.dp))

        TaskStateLabel(ctx.state)
        Spacer(modifier = Modifier.size(4.dp))
        TaskStateIndicator(
            autoPlay = null,
            state = ctx.state,
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text("Шаг: ${ctx.step}")

        Spacer(modifier = Modifier.size(12.dp))

        Text("План")

        Divider(orientation = Orientation.Horizontal)

        if (ctx.plan.isEmpty()) {
            Text("—")
        } else {
            ctx.plan.forEachIndexed { index, item ->
                CheckedItem(
                    checked = ctx.done.contains(item),
                    index = index + 1,
                    name = item,
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text("Текущее")
        Text(if (ctx.current.isBlank()) "—" else ctx.current)
    }
}
