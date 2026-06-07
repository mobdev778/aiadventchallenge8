package com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource

import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatRestApi {

    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Body request: ChatRequestDto
    ): ChatResponseDto
}