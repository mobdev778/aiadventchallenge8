package com.github.mobdev778.aiadventchallenge.data.di

import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.ChatRestApi
import com.github.mobdev778.aiadventchallenge.data.openai.chat.repository.ChatRequestMapper
import com.github.mobdev778.aiadventchallenge.data.openai.chat.repository.ChatResponseMapper
import com.github.mobdev778.aiadventchallenge.data.openai.chat.repository.FinishReasonMapper
import com.github.mobdev778.aiadventchallenge.data.openai.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.data.openai.chat.repository.ReasoningEffortMapper
import com.github.mobdev778.aiadventchallenge.data.openai.chat.repository.RoleMapper
import org.koin.dsl.module
import retrofit2.Retrofit

val dataChatModule = module {
    single<ChatRestApi> {
        val retrofit: Retrofit = get()
        retrofit.create(ChatRestApi::class.java)
    }
    single<RoleMapper> {
        RoleMapper()
    }
    single<FinishReasonMapper> {
        FinishReasonMapper()
    }
    single<ReasoningEffortMapper> {
        ReasoningEffortMapper()
    }
    single<ChatRequestMapper> {
        ChatRequestMapper(
            roleMapper = get(),
            reasoningEffortMapper = get(),
        )
    }
    single<ChatResponseMapper> {
        ChatResponseMapper(
            roleMapper = get(),
            finishReasonMapper = get(),
        )
    }
    single<ChatRepository> {
        ChatRepository(
            restApi = get(),
            requestMapper = get(),
            responseMapper = get(),
        )
    }
}
