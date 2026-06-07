package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.imagegenerator.gpt.GptImageGenerator
import com.github.mobdev778.aiadventchallenge.domain.imagegenerator.svg.SvgImageGenerator
import org.koin.dsl.module

val domainImageGeneratorModule = module {
    single<SvgImageGenerator> {
        SvgImageGenerator(client = get())
    }
    single<GptImageGenerator> {
        GptImageGenerator(chatClient = get(), imageClient = get())
    }
}
