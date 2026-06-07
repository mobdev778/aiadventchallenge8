package com.github.mobdev778.aiadventchallenge.data.openai.chat.repository

import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.openai.chat.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Message

class ChatRequestMapper(
    private val roleMapper: RoleMapper,
    private val reasoningEffortMapper: ReasoningEffortMapper,
) {

    fun map(request: ChatRequest): ChatRequestDto {
        return ChatRequestDto(
            model = request.model,
            reasoningEffort = request.reasoningEffort?.let {
                reasoningEffortMapper.map(it)
            },
            messages = request.messages.map { message ->
                map(message)
            },
            temperature = request.temperature
        )
    }

    private fun map(message: Message): MessageDto {
        return MessageDto(
            role = roleMapper.map(message.role),
            content = message.content
        )
    }
}
