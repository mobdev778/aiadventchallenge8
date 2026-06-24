package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.McpInfoScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.model.McpInfoScreenState
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.ThemedOutlinedTextField
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun McpInfoScreenContent(
    state: McpInfoScreenState,
    onEvent: (McpInfoScreenEvent) -> Unit,
) {
    val server = state.server
    val toolsScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(McpInfoScreenEvent.OnBackClick) }) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Информация о MCP сервере",
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text("Название сервера")
        Text(server?.name ?: "—")

        Spacer(modifier = Modifier.size(12.dp))

        Text("URL")
        Text(server?.url ?: "—")

        Spacer(modifier = Modifier.size(16.dp))

        DefaultButton(
            onClick = { onEvent(McpInfoScreenEvent.OnCheckClick) },
            enabled = server != null && !state.isLoading,
        ) {
            Text(if (state.isLoading) "Проверка..." else "Проверить")
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text("Доступные инструменты")
        ThemedOutlinedTextField(
            value = state.toolsText,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp, max = 320.dp)
                .background(
                    color = LocalContentColor.current.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                )
                .verticalScroll(toolsScrollState),
            singleLine = false,
        )
    }
}
