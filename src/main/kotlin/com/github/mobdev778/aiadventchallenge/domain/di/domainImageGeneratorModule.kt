package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.imagegenerator.gpt.GptImageGenerator
import org.koin.dsl.module

val domainImageGeneratorModule = module {
    single<GptImageGenerator> {
        GptImageGenerator(chatClient = get(), imageClient = get())
    }
}
