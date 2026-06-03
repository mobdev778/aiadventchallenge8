package com.github.mobdev778.aiadventchallenge.app

import com.github.mobdev778.aiadventchallenge.data.di.networkModule
import com.github.mobdev778.aiadventchallenge.data.di.dataOpenApiModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainOpenaiModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainReasoningStrategyModule
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.DirectAnswerStrategy
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.MetaPromptStrategy
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.PanelOfExpertsStrategy
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.ReasoningStrategy
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.StepByStepStrategy
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject

fun main(args: Array<String>) {
    startKoin {
        modules(networkModule)
        modules(dataOpenApiModule)
        modules(domainOpenaiModule)
        modules(domainReasoningStrategyModule)
    }

    val strategies = listOf(
        DirectAnswerStrategy::class.java,
        MetaPromptStrategy::class.java,
        StepByStepStrategy::class.java,
        PanelOfExpertsStrategy::class.java,
    )

    val system = "Ты эксперт по алгоритмам на Kotlin. " +
            "Если тебя просят написать код - пиши только код без каких-либо дополнительных комментариев"
    val task = "Напиши метод\n" +
            "fun radixSort(array: IntArray): IntArray\n" +
            "на Kotlin, который будет выполнять поразрядную сортировку Int-массива"

    runBlocking {
        strategies.forEachIndexed { index, strategyClass ->
            val strategy = inject<ReasoningStrategy>(strategyClass).value
            println("-------------------------------------------------")
            println("Стратегия #${index + 1}: \"${strategy.name}\"")
            val answer = strategy.solve(system, task)
            println("Решение: $answer")
            println()
        }
    }
}