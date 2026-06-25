package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun ChatListScreenContent(
    chats : List<Chat>,
    onEvent : (ChatListScreenEvent) -> Unit,
) {
    var newChatName by remember { mutableStateOf("") }
    val neonHighlightedText = Color(0xFF04D9FF)

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

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                DefaultButton(onClick = { onEvent(ChatListScreenEvent.OnOpenProfilesClick) }) {
                    Text("Профили")
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                DefaultButton(onClick = { onEvent(ChatListScreenEvent.OnOpenMcpClick) }) {
                    Text("MCP")
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                DefaultButton(onClick = { onEvent(ChatListScreenEvent.OnOpenMyMcpClick) }) {
                    Text("MyMCP")
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            color = neonHighlightedText,
            text = "Чаты",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Spacer(modifier = Modifier.size(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) {
            items(chats.size) { index ->
                val chat = chats[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onEvent(ChatListScreenEvent.OnOpenChatClick(chat.id)) },
                        text = chat.name,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    DefaultButton(
                        onClick = { onEvent(ChatListScreenEvent.OnDeleteChatClick(chat.id)) },
                    ) {
                        Text("Удалить")
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            }
        }

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