package com.github.mobdev778.aiadventchallenge.data.openai.repository

import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.ChatResponseDto
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.ChoiceDto
import com.github.mobdev778.aiadventchallenge.data.openai.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.domain.openai.ChatResponse
import com.github.mobdev778.aiadventchallenge.domain.openai.Choice
import com.github.mobdev778.aiadventchallenge.domain.openai.Message

class ChatResponseMapper(
    private val roleMapper: RoleMapper,
    private val finishReasonMapper: FinishReasonMapper,
) {

    fun map(dto: ChatResponseDto): ChatResponse {
        return ChatResponse(
            dto.choices.map { choiceDto ->
                map(choiceDto)
            }
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