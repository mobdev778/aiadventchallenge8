package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.openai.OpenAIClient
import org.koin.dsl.module

val domainOpenaiModule = module {
    single<OpenAIClient> {
        OpenAIClient(repository = get())
    }
}
