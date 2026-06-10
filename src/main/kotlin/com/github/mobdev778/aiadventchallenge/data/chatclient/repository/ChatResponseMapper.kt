package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChatResponseDto
import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChoiceDto
import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Choice
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import org.koin.core.annotation.Single

@Single
class ChatResponseMapper(
    private val roleMapper: RoleMapper,
    private val finishReasonMapper: FinishReasonMapper,
    private val usageMapper: UsageMapper,
) {

    fun map(dto: ChatResponseDto): ChatResponse {
        return ChatResponse(
            choices = dto.choices.map { choiceDto ->
                map(choiceDto)
            },
            usage = dto.usage?.let {
                usageMapper.map(it)
            },
        )
    }

    private fun map(choiceDto: ChoiceDto): Choice {
        return Choice(
            message = map(choiceDto.message),
            finishReason = finishReasonMapper.map(choiceDto.finishReason),
        )
    }

    private fun map(messageDto: MessageDto): Message {
        return Message(
            role = roleMapper.map(messageDto.role),
            content = messageDto.content,
        )
    }
}