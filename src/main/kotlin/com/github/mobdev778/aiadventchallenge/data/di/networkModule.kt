package com.github.mobdev778.aiadventchallenge.data.di

import com.github.mobdev778.aiadventchallenge.domain.profile.AppProfile
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        val appProfile: AppProfile = get()

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer ${appProfile.apiKey}")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    single<Retrofit> {
        val contentType = "application/json; charset=utf-8".toMediaType()
        val json: Json = get()
        val okHttpClient: OkHttpClient = get()
        val appProfile: AppProfile = get()

        Retrofit.Builder()
            .baseUrl(appProfile.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}