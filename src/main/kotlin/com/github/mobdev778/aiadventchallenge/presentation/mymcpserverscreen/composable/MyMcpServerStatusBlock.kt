package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.MyMcpServerScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.model.MyMcpServerScreenState
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun MyMcpServerStatusBlock(
    state: MyMcpServerScreenState,
    onEvent: (MyMcpServerScreenEvent) -> Unit,
) {
    val neonHighlightedText = Color(0xFF04D9FF)
    val neonPink = Color(0xFFFE019A) // неоновый розовый
    val neonGreen = Color(0xFF39FF14) // неоновый зеленый

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Состояние сервера",
        color = neonHighlightedText,
        textAlign = TextAlign.Center,
    )

    Text("URL: ${state.server.url}")

    Row {
        Text("Статус: ")
        if (state.server.isRunning) {
            Text(
                text = "Запущен",
                color = neonGreen,
            )
        } else {
            Text(
                text = "Остановлен",
                color = neonPink,
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        OutlinedButton(onClick = { onEvent(MyMcpServerScreenEvent.OnStartStopClick) }) {
            if (state.server.isRunning) {
                Text(
                    text = "Остановить",
                    color = neonPink,
                )
            } else {
                Text(
                    text = "Запустить",
                    color = neonGreen,
                )
            }
        }
    }
}