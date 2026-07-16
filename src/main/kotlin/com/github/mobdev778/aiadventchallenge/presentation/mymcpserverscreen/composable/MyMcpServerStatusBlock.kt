package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerState
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.MyMcpServerScreenEvent
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

// Приватные константы цветов для визуального оформления блока статуса MCP-сервера
@Suppress("MagicNumber")
private val NeonHighlightedTextColor = Color(0xFF04D9FF)
@Suppress("MagicNumber")
private val NeonPinkColor = Color(0xFFFE019A)
@Suppress("MagicNumber")
private val NeonGreenColor = Color(0xFF39FF14)

/**
 * Составной элемент экрана «Мои MCP-серверы», отображающий полную информацию о состоянии
 * конкретного MCP-сервера: название, описание, URL, текущий статус (запущен/остановлен)
 * и кнопку для запуска или остановки сервера.
 *
 * Статус отображается цветным текстом (зелёный — «Запущен», розовый — «Остановлен»).
 * При нажатии на кнопку в зависимости от текущего состояния через [onEvent] отправляется
 * событие [MyMcpServerScreenEvent.OnStartClick] или [MyMcpServerScreenEvent.OnStopClick].
 *
 * @param state Текущее состояние и конфигурация сервера. Содержит имя, описание, URL,
 *              флаг [MyMcpServerState.isRunning] и другие параметры.
 * @param onEvent Лямбда-коллбек для отправки пользовательских событий, влияющих на
 *                экран и управление сервером.
 */
@Composable
fun MyMcpServerStatusBlock(
    state: MyMcpServerState,
    onEvent: (MyMcpServerScreenEvent) -> Unit,
) {


    Spacer(Modifier.size(8.dp))

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = state.name,
        color = NeonHighlightedTextColor,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.size(8.dp))
    Text(
        text = "Описание: ${state.description}",
    )

    Text("URL: ${state.url}")

    Row {
        Text("Статус: ")
        if (state.isRunning) {
            Text(
                text = "Запущен",
                color = NeonGreenColor,
            )
        } else {
            Text(
                text = "Остановлен",
                color = NeonPinkColor,
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        OutlinedButton(
            onClick = {
                when (state.isRunning) {
                    true -> onEvent(MyMcpServerScreenEvent.OnStopClick(state.name))
                    false -> onEvent(MyMcpServerScreenEvent.OnStartClick(state.name))
                }
            }
        ) {
            if (state.isRunning) {
                Text(
                    text = "Остановить",
                    color = NeonPinkColor,
                )
            } else {
                Text(
                    text = "Запустить",
                    color = NeonGreenColor,
                )
            }
        }
    }
}
