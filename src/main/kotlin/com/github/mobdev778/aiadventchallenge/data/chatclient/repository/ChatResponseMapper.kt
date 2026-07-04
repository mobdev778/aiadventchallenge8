package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChatResponseDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatResponse
import org.koin.core.annotation.Single

@Single
class ChatResponseMapper(
    private val usageMapper: UsageMapper,
    private val choiceMapper: ChoiceMapper,
) {

    fun map(dto: ChatResponseDto): ChatResponse {
        return ChatResponse(
            choices = dto.choices.map { choiceDto ->
                choiceMapper.map(choiceDto)
            },
            usage = dto.usage?.let {
                usageMapper.map(it)
            },
        )
    }
}
