package com.github.mobdev778.aiadventchallenge.domain.messageselection

import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage

class MessageLimitStrategy(val maxMessages: Int) : MessageSelectionStrategy {

    override suspend fun selectMessages(history: List<ChatMessage>): List<ChatMessage> {
        return history.takeLast(maxMessages)
    }
}