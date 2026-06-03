package com.github.mobdev778.aiadventchallenge.data.openai.datasource

import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIRestApi {

    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Body request: ChatRequestDto
    ): ChatResponseDto
}