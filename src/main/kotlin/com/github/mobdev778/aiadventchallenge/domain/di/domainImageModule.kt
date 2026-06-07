package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.openai.image.ImageClient
import org.koin.dsl.module

val domainImageModule = module {
    single<ImageClient> {
        ImageClient(repository = get())
    }
}