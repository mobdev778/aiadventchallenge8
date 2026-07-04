package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.UsageDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Usage
import org.koin.core.annotation.Single

@Single
class UsageMapper {

    fun map(dto: UsageDto): Usage {
        return Usage(
            promptTokens = dto.promptTokens,
            completionTokens = dto.completionTokens,
            totalTokens = dto.totalTokens,
        )
    }
}
