package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.MyMcpServerScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.model.MyMcpServerScreenState
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun MyMcpServerActionsBlock(
    state: MyMcpServerScreenState,
    onEvent: (MyMcpServerScreenEvent) -> Unit,
) {
    val neonHighlightedText = Color(0xFF04D9FF)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            enabled = state.actionEnabled,
            onClick = { onEvent(MyMcpServerScreenEvent.OnResetClick) },
        ) {
            Text(
                text = "Сбросить",
                color = neonHighlightedText,
            )
        }
        OutlinedButton(
            enabled = state.actionEnabled,
            onClick = { onEvent(MyMcpServerScreenEvent.OnSaveClick) },
        ) {
            Text(
                text = "Сохранить",
                color = neonHighlightedText,
            )
        }
    }
}