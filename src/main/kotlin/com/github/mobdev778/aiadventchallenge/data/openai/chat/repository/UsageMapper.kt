package com.github.mobdev778.aiadventchallenge.data.openai.chat.repository

import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.UsageDto
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Usage

class UsageMapper {

    fun map(dto: UsageDto): Usage {
        return Usage(
            promptTokens = dto.promptTokens,
            completionTokens = dto.completionTokens,
            totalTokens = dto.totalTokens,
        )
    }
}