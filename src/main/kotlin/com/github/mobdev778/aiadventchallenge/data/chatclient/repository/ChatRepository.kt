package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.ChatRestApi
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatResponse
import org.koin.core.annotation.Single
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit

@Single
class ChatRepository(
    private val requestMapper: ChatRequestMapper,
    private val responseMapper: ChatResponseMapper,
) {

    suspend fun sendRequest(
        request: ChatRequest,
    ): ChatResponse {
        // настройки доступа к модели могут меняться на ходу, поэтому мы каждый раз строим ретрофит заново
        val retrofit: Retrofit by inject(Retrofit::class.java)
        val restApi: ChatRestApi = retrofit.create(ChatRestApi::class.java)
        val requestDto = requestMapper.map(request)
        val responseDto = restApi.postChatCompletions(requestDto)
        return responseMapper.map(responseDto)
    }
}