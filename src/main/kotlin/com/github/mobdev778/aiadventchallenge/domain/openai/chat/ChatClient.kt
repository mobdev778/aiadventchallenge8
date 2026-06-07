package com.github.mobdev778.aiadventchallenge.domain.openai.chat

import com.github.mobdev778.aiadventchallenge.data.openai.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatResponse

class ChatClient(
    private val repository: ChatRepository,
) {
    suspend fun execute(request: ChatRequest): ChatResponse {
        return repository.getChatCompletion(request)
    }
}