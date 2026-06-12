package com.github.mobdev778.aiadventchallenge.domain.chathistory.model

import java.util.UUID

data class ChatMessage(
    val id: UUID,
    val parentId: UUID?,
    val time: Long,
    val text: String,
    val author: ChatAuthor,
    val tokens: Int,
    // ранг - количество "сжатий контекста", с которым связано данное сообщение. У оригинальных сообщений ранг: 0.
    val rank: Int,
)