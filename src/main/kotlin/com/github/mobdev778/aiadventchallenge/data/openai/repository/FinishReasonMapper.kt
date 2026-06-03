package com.github.mobdev778.aiadventchallenge.data.openai.repository

import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.FinishReasonDto
import com.github.mobdev778.aiadventchallenge.domain.openai.FinishReason

class FinishReasonMapper {

    fun map(reason: FinishReasonDto): FinishReason {
        return when (reason) {
            FinishReasonDto.Stop -> FinishReason.Stop
            FinishReasonDto.Length -> FinishReason.Length
            FinishReasonDto.ToolCalls -> FinishReason.ToolCalls
            FinishReasonDto.ContentFilter -> FinishReason.ContentFilter
            FinishReasonDto.RleMaxTokens -> FinishReason.RleMaxTokens
        }
    }
}