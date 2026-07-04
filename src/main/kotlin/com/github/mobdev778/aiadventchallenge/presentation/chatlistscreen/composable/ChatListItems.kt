package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.ChatListScreenEvent
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ChatListItems(
    chats: List<Chat>,
    modifier: Modifier = Modifier.Companion,
    onEvent: (ChatListScreenEvent) -> Unit,
) {
    LazyColumn(modifier = modifier) {
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
}
