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
    lowLevelExample()
}

private fun lowLevelExample() {
    val retrofit: Retrofit by inject(Retrofit::class.java)
    val api = retrofit.create(OpenAIRestApi::class.java)

    runBlocking {
        println("══════════════════════════════════════════")
        println("🔓 ЗАПРОС БЕЗ ОГРАНИЧЕНИЙ")
        println("══════════════════════════════════════════")
        sendRequest(api, buildRequest(userPrompt, limited = false))

        println("\n══════════════════════════════════════════")
        println("🔒 ЗАПРОС С ОГРАНИЧЕНИЯМИ (API + промпт)")
        println("══════════════════════════════════════════")
        sendRequest(api, buildRequest(userPrompt, limited = true))
    }
}

private fun buildRequest(userRequest: String, limited: Boolean): ChatRequestDto {
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

    return when {
        limited -> ChatRequestDto(
            model = BuildConfig.MODEL,
            messages = listOf(systemPrompt, userPrompt),
            temperature = 0.7,
            maxTokens = 200,
            stop = listOf("###END###"),
        )
        else -> ChatRequestDto(
            model = BuildConfig.MODEL,
            messages = listOf(systemPrompt, userPrompt),
            temperature = 0.7,
        )
    }
}

private suspend fun sendRequest(api: OpenAIRestApi, request: ChatRequestDto) {
    val response = api.getChatCompletion(request)
    println("─────────────────────────────────────────────")
    response.choices.forEach { choice ->
        val msg = choice.message
        println("${msg.role}:")
        println(msg.content)
    }
}