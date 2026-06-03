package com.github.mobdev778.aiadventchallenge.domain.openai

import com.github.mobdev778.aiadventchallenge.data.openai.repository.OpenAIRepository

class OpenAIClient(
    private val repository: OpenAIRepository,
) {
    suspend fun execute(request: ChatRequest): ChatResponse {
        return repository.getChatCompletion(request)
    }
}