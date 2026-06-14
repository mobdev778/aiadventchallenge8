package com.github.mobdev778.aiadventchallenge.domain.chat.model

import java.util.UUID

data class ChatMessage(
    val id: UUID,
    val chatId: UUID,
    val parentId: UUID?,
    val branchB: Boolean, // показывает, что в данный момент используется альтернативная ветка
    val time: Long,
    val text: String,
    val type: MessageType,
    val tokens: Int,
    // ранг - количество "сжатий контекста", с которым связано данное сообщение. У оригинальных сообщений ранг: 0.
    val rank: Int,
)