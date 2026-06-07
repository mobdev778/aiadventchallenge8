package com.github.mobdev778.aiadventchallenge.data.openai.chat.repository

import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.ChatRestApi
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatResponse

class ChatRepository(
    private val restApi: ChatRestApi,
    private val requestMapper: ChatRequestMapper,
    private val responseMapper: ChatResponseMapper,
) {

    suspend fun getChatCompletion(
        request: ChatRequest,
    ): ChatResponse {
        val requestDto = requestMapper.map(request)
        val responseDto = restApi.getChatCompletion(requestDto)
        return responseMapper.map(responseDto)
    }
}