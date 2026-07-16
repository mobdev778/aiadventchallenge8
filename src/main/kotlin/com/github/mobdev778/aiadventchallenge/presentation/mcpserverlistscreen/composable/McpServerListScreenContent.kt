package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen.McpServerListScreenEvent
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Основное содержимое экрана со списком MCP-серверов.
 *
 * Отображает панель инструментов с кнопками «Назад» и «Добавить», заголовок «MCP серверы»,
 * а также список доступных серверов. Каждый элемент списка представляет собой строку
 * [McpServerRow], которая позволяет управлять активностью сервера, просматривать детали
 * и удалять его. При отсутствии серверов выводится информационное сообщение.
 *
 * @param servers Список объектов [McpServer], отображаемых на экране.
 * @param onEvent Колбэк для обработки событий [McpServerListScreenEvent], инициированных
 *                пользователем (возврат, добавление, изменение активности, выбор или удаление сервера).
 */
@Composable
fun McpServerListScreenContent(
    servers: List<McpServer>,
    onEvent: (McpServerListScreenEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(McpServerListScreenEvent.OnBackClick) }) {
                Text("Назад")
            }

            DefaultButton(onClick = { onEvent(McpServerListScreenEvent.OnAddClick) }) {
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "MCP серверы",
        )

        if (servers.isEmpty()) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "Серверов пока нет. Нажмите 'Добавить'.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = servers,
                    key = { it.id },
                ) { server ->
                    McpServerRow(
                        server = server,
                        onCheckedChange = { checked ->
                            onEvent(McpServerListScreenEvent.OnActiveChanged(server.id, checked))
                        },
                        onClick = {
                            onEvent(McpServerListScreenEvent.OnServerClick(server.id))
                        },
                        onDeleteClick = {
                            onEvent(McpServerListScreenEvent.OnDeleteClick(server.id))
                        },
                    )
                }
            }
        }
    }
}
