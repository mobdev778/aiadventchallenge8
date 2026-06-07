package com.github.mobdev778.aiadventchallenge.data.di

import com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.ImageRestApi
import com.github.mobdev778.aiadventchallenge.data.openai.image.repository.ImageDataMapper
import com.github.mobdev778.aiadventchallenge.data.openai.image.repository.ImageRepository
import com.github.mobdev778.aiadventchallenge.data.openai.image.repository.ImageRequestMapper
import com.github.mobdev778.aiadventchallenge.data.openai.image.repository.ImageResponseMapper
import org.koin.dsl.module
import retrofit2.Retrofit

val dataImageModule = module {
    single<ImageRestApi> {
        val retrofit: Retrofit = get()
        retrofit.create(ImageRestApi::class.java)
    }
    single<ImageDataMapper> {
        ImageDataMapper()
    }
    single<ImageRequestMapper> {
        ImageRequestMapper()
    }
    single<ImageResponseMapper> {
        ImageResponseMapper(
            imageDataMapper = get(),
        )
    }
    single<ImageRepository> {
        ImageRepository(
            restApi = get(),
            requestMapper = get(),
            responseMapper = get(),
        )
    }
}
