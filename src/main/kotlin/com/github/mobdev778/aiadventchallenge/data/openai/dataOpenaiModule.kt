package com.github.mobdev778.aiadventchallenge.data.openai

import com.github.mobdev778.aiadventchallenge.data.openai.datasource.OpenAIRestApi
import com.github.mobdev778.aiadventchallenge.data.openai.repository.ChatRequestMapper
import com.github.mobdev778.aiadventchallenge.data.openai.repository.ChatResponseMapper
import com.github.mobdev778.aiadventchallenge.data.openai.repository.OpenAIRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val dataOpenApiModule = module {
    single<OpenAIRestApi> {
        val retrofit: Retrofit = get()
        retrofit.create(OpenAIRestApi::class.java)
    }
    single<ChatRequestMapper> {
        ChatRequestMapper()
    }
    single<ChatResponseMapper> {
        ChatResponseMapper()
    }
    single<OpenAIRepository> {
        OpenAIRepository(
            openAIRestApi = get(),
            requestMapper = get(),
            responseMapper = get(),
        )
    }
}
