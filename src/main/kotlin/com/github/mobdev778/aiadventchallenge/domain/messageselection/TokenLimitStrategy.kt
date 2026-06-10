package com.github.mobdev778.aiadventchallenge.domain.messageselection

import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage

class TokenLimitStrategy(val maxTokens: Int) : MessageSelectionStrategy {

    override fun selectMessages(history: List<ChatMessage>): List<ChatMessage> {
        val result = mutableListOf<ChatMessage>()
        var sum = 0

        for (msg in history.reversed()) {
            val tokens = when {
                msg.tokens > 0 -> msg.tokens
                else -> estimateTokens(msg)
            }
            if (sum + tokens > maxTokens) break
            result.add(0, msg)
            sum += tokens
        }

        return result
    }

    private fun estimateTokens(msg: ChatMessage): Int = msg.text.length / 4 // Грубая оценка
}