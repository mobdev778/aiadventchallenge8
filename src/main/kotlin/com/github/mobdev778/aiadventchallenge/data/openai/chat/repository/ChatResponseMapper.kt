package com.github.mobdev778.aiadventchallenge.data.openai.chat.repository

import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.ChatResponseDto
import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.ChoiceDto
import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatResponse
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Choice
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Message

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