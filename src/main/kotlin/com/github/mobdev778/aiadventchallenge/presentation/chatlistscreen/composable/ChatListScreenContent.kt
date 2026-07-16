package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.ChatListScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.ThemedOutlinedTextField
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Цвет для выделенного заголовка в стиле неонового свечения.
 */
@Suppress("MagicNumber")
private val NeonHighlightedTextColor = Color(0xFF04D9FF)

/**
 * Основное содержимое экрана списка чатов.
 *
 * Компонент объединяет верхнюю панель с кнопкой настроек, заголовок, прокручиваемый список
 * чатов (через [ChatListItems]) и нижнюю область с полем ввода для создания нового чата.
 * Все действия пользователя передаются во внешний обработчик в виде событий
 * [ChatListScreenEvent].
 *
 * @param chats Список чатов для отображения.
 * @param onEvent Обработчик событий экрана чатов (открытие настроек, открытие чата,
 *   создание нового, удаление и т.п.).
 */
@Composable
fun ChatListScreenContent(
    chats : List<Chat>,
    onEvent : (ChatListScreenEvent) -> Unit,
) {
    var newChatName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DefaultButton(onClick = { onEvent(ChatListScreenEvent.OnOpenSettingsClick) }) {
                    Text("Настройки")
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            color = NeonHighlightedTextColor,
            text = "Чаты",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Spacer(modifier = Modifier.size(8.dp))

        ChatListItems(
            chats = chats,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            onEvent = onEvent,
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemedOutlinedTextField(
                modifier = Modifier.weight(1f),
                value = newChatName,
                onValueChange = { newChatName = it },
            )

            DefaultButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = {
                    val name = newChatName
                    newChatName = ""
                    onEvent(ChatListScreenEvent.OnCreateChatClick(name))
                },
            ) {
                Text("Создать")
            }
        }
    }
}
