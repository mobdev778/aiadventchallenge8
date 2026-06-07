package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.openai.chat.ChatClient
import org.koin.dsl.module

val domainChatModule = module {
    single<ChatClient> {
        ChatClient(repository = get())
    }
}
