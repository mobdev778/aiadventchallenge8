package com.github.mobdev778.aiadventchallenge.data.network

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    @Single
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    @Factory
    fun createOkHttpClient(settingsRepository: SettingsRepository): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val apiKey = runBlocking {
            settingsRepository.getSettings().apiKey
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    @Factory
    fun createRetrofit(
        json: Json,
        okHttpClient: OkHttpClient,
        settingsRepository: SettingsRepository,
    ): Retrofit {
        val contentType = "application/json; charset=utf-8".toMediaType()

        val baseUrl = runBlocking {
            settingsRepository.getSettings().baseUrl
        }
        val finalBaseUrl = when {
            baseUrl.isEmpty() -> "http://127.0.0.1:1234"
            baseUrl.endsWith("/") -> baseUrl
            else -> "$baseUrl/"
        }

        return Retrofit.Builder()
            .baseUrl(finalBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}