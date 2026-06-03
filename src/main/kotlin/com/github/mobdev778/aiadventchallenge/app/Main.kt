package com.github.mobdev778.aiadventchallenge.app

import com.embeddings.rag.BuildConfig
import com.github.mobdev778.aiadventchallenge.data.common.networkModule
import com.github.mobdev778.aiadventchallenge.data.openai.dataOpenApiModule
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.OpenAIRestApi
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.data.openai.repository.OpenAIRepository
import com.github.mobdev778.aiadventchallenge.domain.openai.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.Message
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
        val responseDto = api.getChatCompletion(
            ChatRequestDto(
                model = BuildConfig.MODEL,
                messages = listOf(
                    MessageDto(
                        role = "system",
                        content = "Ты - специалист по сочинению анекдотов. " +
                                "Когда юзер пришлет тебе слово, выполни задачу: Сочини анекдот про [UserRequest]",
                    ),
                    MessageDto(
                        role = "user",
                        content = "программистов"
                    ),
                ),
                temperature = 0.7,
            )
        )
        println("Ответ LLM-ки:")
        for (choice in responseDto.choices) {
            val message = choice.message
            println("${message.role}: ${message.content}")
        }
    }
}