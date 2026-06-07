package com.github.mobdev778.aiadventchallenge.app

import com.github.mobdev778.aiadventchallenge.data.di.networkModule
import com.github.mobdev778.aiadventchallenge.data.di.dataChatModule
import com.github.mobdev778.aiadventchallenge.data.di.dataImageModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainChatModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainImageGeneratorModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainImageModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainReasoningStrategyModule
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatResponse
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Message
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Role
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import kotlin.time.measureTime

fun main(args: Array<String>) {
    startKoin {
        modules(networkModule)
        modules(dataChatModule)
        modules(dataImageModule)
        modules(domainChatModule)
        modules(domainImageModule)
        modules(domainReasoningStrategyModule)
        modules(domainImageGeneratorModule)
    }

    val chatClient: ChatClient by inject(ChatClient::class.java)

    val models = listOf(
        "gpt-3.5-turbo",
        "gpt-4.1",
        "gpt-5.4",
    )

    runBlocking {
        for (model in models) {
            val prompt = "Напиши пример Kotlin-функции, которая выполняет Radix-сортировку IntArray. " +
                    "Только код функции, без лишних комментариев. Исключи из вывода ```kotlin```"

            println("-------------------------------------")
            println("модель: $model")

            val response: ChatResponse
            val duration = measureTime {
                response = chatClient.execute(
                    ChatRequest(
                        model = model,
                        messages = listOf(
                            Message(Role.User, prompt)
                        ),
                    )
                )
            }
            val code = response.choices.firstOrNull()?.message?.content ?: "- no response -"

            val fileName = "radixSort_" +
                    model.replace(".", "").replace("-", "")
            File("${System.getProperty("user.dir")}/${fileName}.kt").writeText(code)
            println("Запрос выполнен за: ${duration.inWholeMilliseconds} мс")
            println("Потрачено токенов: ${response.usage?.totalTokens}")
        }
    }
}