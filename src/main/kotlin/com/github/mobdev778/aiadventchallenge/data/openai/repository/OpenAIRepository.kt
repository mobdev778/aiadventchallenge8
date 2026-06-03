package com.github.mobdev778.aiadventchallenge.data.openai.repository

import com.github.mobdev778.aiadventchallenge.data.openai.datasource.OpenAIRestApi
import com.github.mobdev778.aiadventchallenge.domain.openai.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.ChatResponse
import okhttp3.Request

class OpenAIRepository(
    private val openAIRestApi: OpenAIRestApi,
    private val requestMapper: ChatRequestMapper,
    private val responseMapper: ChatResponseMapper,
) {

    suspend fun getChatCompletion(
        request: ChatRequest,
    ): ChatResponse {
        val requestDto = requestMapper.map(request)
        val responseDto = openAIRestApi.getChatCompletion(requestDto)
        return responseMapper.map(responseDto)
    }
}