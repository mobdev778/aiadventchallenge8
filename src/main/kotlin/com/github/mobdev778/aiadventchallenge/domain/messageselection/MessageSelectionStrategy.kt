package com.github.mobdev778.aiadventchallenge.domain.messageselection

import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage

interface MessageSelectionStrategy {

    fun selectMessages(
        history: List<ChatMessage>,
    ): List<ChatMessage>
}