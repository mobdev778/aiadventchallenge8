package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.DirectAnswerStrategy
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.MetaPromptStrategy
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.PanelOfExpertsStrategy
import com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy.StepByStepStrategy
import org.koin.dsl.module

val domainReasoningStrategyModule = module {
    single<DirectAnswerStrategy> {
        DirectAnswerStrategy(appProfile = get(), client = get())
    }

    single<MetaPromptStrategy> {
        MetaPromptStrategy(directAnswerStrategy = get())
    }

    single<StepByStepStrategy> {
        StepByStepStrategy(appProfile = get(), client = get())
    }

    single<PanelOfExpertsStrategy> {
        PanelOfExpertsStrategy(directAnswerStrategy = get())
    }
}