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

/**
 * Модуль Koin, предоставляющий сетевые зависимости: парсер JSON, HTTP-клиент и экземпляр Retrofit.
 *
 * Все компоненты настраиваются с использованием значений из репозитория настроек ([SettingsRepository]),
 * что позволяет динамически подстраивать параметры подключения к API (ключ, базовый URL).
 */
@Module
class NetworkModule {

    /**
     * Создаёт и предоставляет экземпляр [Json] с предустановленными настройками:
     * - Игнорирование неизвестных ключей при десериализации (`ignoreUnknownKeys = true`).
     * - Принудительное приведение значений к ожидаемым типам (`coerceInputValues = true`).
     *
     * @return сконфигурированный объект [Json].
     */
    @Single
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    /**
     * Создаёт настроенный HTTP-клиент [OkHttpClient] для использования в Retrofit.
     *
     * Настройки включают:
     * - Логирование тела запросов и ответов через [HttpLoggingInterceptor].
     * - Автоматическое добавление заголовка `Authorization` с API-ключом, полученным из [SettingsRepository].
     * - Таймауты подключения, чтения и записи, заданные константами в [NetworkModule.Companion].
     *
     * @param settingsRepository репозиторий настроек, откуда извлекается API-ключ.
     * @return экземпляр [OkHttpClient] с применённой конфигурацией.
     */
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
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    /**
     * Создаёт экземпляр [Retrofit], сконфигурированный для работы с API.
     *
     * Базовый URL извлекается из настроек через [SettingsRepository]. Если он пуст, используется
     * локальный адрес `http://127.0.0.1:1234` (локальное LLM-API). К URL добавляется завершающий слеш,
     * что требуется для корректного формирования запросов.
     *
     * В качестве конвертера используется [kotlinx.serialization], настроенный через переданный [Json].
     *
     * @param json объект [Json] для сериализации/десериализации.
     * @param okHttpClient предварительно настроенный HTTP-клиент.
     * @param settingsRepository репозиторий настроек для получения базового URL.
     * @return сконфигурированный экземпляр [Retrofit].
     */
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
        /** Таймаут установки соединения в секундах. */
        private const val CONNECT_TIMEOUT_SECONDS = 600L
        /** Таймаут чтения данных в секундах. */
        private const val READ_TIMEOUT_SECONDS = 600L
        /** Таймаут записи данных в секундах. */
        private const val WRITE_TIMEOUT_SECONDS = 600L
    }
}
