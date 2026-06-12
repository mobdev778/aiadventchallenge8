package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage

data class ChatUiMessage(
    val message: ChatMessage,
    val rank: Int,
    val insideWindow: Boolean,         // признак попадания сообщения "в окно"
    val children: List<ChatUiMessage>, // список из "детей" текущего сообщения
    val expanded: Boolean,             // признак того, раскрыт список детей или не раскрыт
)