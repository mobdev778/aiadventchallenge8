package com.github.mobdev778.aiadventchallenge.domain.chatclient

import com.github.mobdev778.aiadventchallenge.data.chatclient.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatResponse
import org.koin.core.annotation.Single

@Single
class ChatClient(
    private val repository: ChatRepository,
) {
    suspend fun execute(request: ChatRequest): ChatResponse {
        return repository.sendRequest(request)
    }
}