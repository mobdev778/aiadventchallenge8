package com.github.mobdev778.aiadventchallenge.data.openai.chat.repository

import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.ReasoningEffortDto
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ReasoningEffort

class ReasoningEffortMapper {

    fun map(reasoningEffort: ReasoningEffort): ReasoningEffortDto {
        return when (reasoningEffort) {
            ReasoningEffort.High -> ReasoningEffortDto.High
            ReasoningEffort.Medium -> ReasoningEffortDto.Medium
            ReasoningEffort.Low -> ReasoningEffortDto.Low
        }
    }
}