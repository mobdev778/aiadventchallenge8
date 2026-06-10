package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ReasoningEffortDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ReasoningEffort
import org.koin.core.annotation.Single

@Single
class ReasoningEffortMapper {

    fun map(reasoningEffort: ReasoningEffort): com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ReasoningEffortDto {
        return when (reasoningEffort) {
            ReasoningEffort.High -> ReasoningEffortDto.High
            ReasoningEffort.Medium -> ReasoningEffortDto.Medium
            ReasoningEffort.Low -> ReasoningEffortDto.Low
        }
    }
}