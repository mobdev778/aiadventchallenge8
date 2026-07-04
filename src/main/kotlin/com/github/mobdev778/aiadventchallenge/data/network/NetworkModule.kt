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
import org.slf4j.LoggerFactory
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
        val httpLogger = LoggerFactory.getLogger("HTTP")
        val logging = HttpLoggingInterceptor { message ->
            println("!!! network: $message")
            httpLogger.info(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val apiKey = runBlocking {
            settingsRepository.getSettings().apiKey
                .replace("\n", "")
                .trim()
        }

        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
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
                .replace("\n", "")
                .trim()
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

    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 600L
    }
}
