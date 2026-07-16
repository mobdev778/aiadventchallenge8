package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Composable-элемент строки списка для отображения одного сервера MCP.
 *
 * Отображает чекбокс активности, имя сервера, его URL (сокращённый при необходимости) и кнопку «Удалить».
 * Локальные серверы имеют ограниченную возможность изменения состояния чекбокса и не могут быть удалены.
 *
 * @param server Модель сервера MCP, данные которой отображаются в строке.
 * @param onCheckedChange Колбэк, вызываемый при изменении состояния чекбокса активности.
 *                        Принимает новое булево значение.
 * @param onClick Колбэк, вызываемый при клике на строку (исключая чекбокс и кнопку).
 * @param onDeleteClick Колбэк, вызываемый при нажатии кнопки удаления.
 */
@Composable
fun McpServerRow(
    server: McpServer,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = server.active,
            enabled = !server.isLocal,
            onCheckedChange = onCheckedChange,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = server.name
            )
            Text(
                text = if (server.url.length > VISIBLE_URL_LENGTH) {
                    server.url.take(VISIBLE_URL_LENGTH) + "..."
                } else {
                    server.url
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        DefaultButton(
            onClick = onDeleteClick,
            enabled = !server.isLocal
        ) {
            Text(text = "Удалить")
        }
    }
}

/**
 * Максимальная длина URL-адреса, отображаемого в строке сервера без сокращения.
 * Если фактическая длина превышает это значение, URL обрезается и дополняется троеточием.
 */
const val VISIBLE_URL_LENGTH = 35
