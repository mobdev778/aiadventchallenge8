package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.MyMcpServerScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.model.MyMcpServerScreenState
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun MyMcpServerScreenContent(
    state: MyMcpServerScreenState,
    onEvent: (MyMcpServerScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { onEvent(MyMcpServerScreenEvent.OnBackClick) }) {
                Text("Back")
            }
        }

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "My MCP Server",
        )

        MyMcpServerSettingsBlock(
            state = state,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        MyMcpServerStatusBlock(
            state = state,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        MyMcpServerActionsBlock(
            state = state,
            onEvent = onEvent,
        )
    }
}

