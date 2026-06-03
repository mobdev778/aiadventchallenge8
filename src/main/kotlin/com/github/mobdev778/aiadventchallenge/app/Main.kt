package com.github.mobdev778.aiadventchallenge.app

import com.embeddings.rag.BuildConfig
import com.github.mobdev778.aiadventchallenge.data.common.networkModule
import com.github.mobdev778.aiadventchallenge.data.openai.dataOpenApiModule
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.OpenAIRestApi
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.RoleDto
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit

fun main() {
    startKoin {
        modules(networkModule)
        modules(dataOpenApiModule)
    }

    val retrofit: Retrofit by inject(Retrofit::class.java)
    val api = retrofit.create(OpenAIRestApi::class.java)

    val userPrompt = "Расскажи о преимуществах языка Kotlin для Android-разработки."

    runBlocking {
        for (i in 0..3) {
            val useStopMarker = (i and 1) != 0
            val limitTokens = (i shr 1) and 1 != 0
            println("══════════════════════════════════════════")
            println("🔓 ЗАПРОС №${i}.\n" +
                    "Ограничение токенов: ${limitTokens},\n" +
                    "стоп-маркер: ${useStopMarker}")
            println("══════════════════════════════════════════")
            sendRequest(api, buildRequest(userPrompt, limitTokens, useStopMarker))
        }
    }
}

private fun buildRequest(userRequest: String, limitTokens: Boolean, useStopMarker: Boolean): ChatRequestDto {
    val systemPrompt = MessageDto(
        role = RoleDto.System,
        content = """
                    Ты — эксперт по мобильной разработке.
                    Отвечай СТРОГО в следующем формате:
                    
                    1. <пункт 1>
                    2. <пункт 2>
                    3. <пункт 3>
                    
                    Правила:
                    - Ровно 3 пункта, нумерованный список.
                    - Каждый пункт — одно предложение, не длиннее 20 слов.
                    - Общий объём ответа — не более 120 слов.
                    - Никаких вступлений, заключений и лишних пояснений.
                    - Сразу после последнего пункта выведи маркер ###END### и заверши ответ.
                """.trimIndent()
    )
    val userPrompt = MessageDto(
        role = RoleDto.User,
        content = userRequest
    )

    return ChatRequestDto(
        model = BuildConfig.MODEL,
        messages = listOf(
            systemPrompt, userPrompt
        ),
        temperature = 0.7,
        maxTokens = when {
            limitTokens -> 200
            else -> null
        },
        stop = when {
            useStopMarker -> listOf("###END###")
            else -> null
        },
    )
}

private suspend fun sendRequest(api: OpenAIRestApi, request: ChatRequestDto) {
    val response = api.getChatCompletion(request)
    println("─────────────────────────────────────────────")
    response.choices.forEach { choice ->
        val msg = choice.message
        println("${msg.role}:")
        println(msg.content)
    }
    println("─────────────────────────────────────────────")
    println("finish_reason: ${response.choices.firstOrNull()?.finishReason}")
}