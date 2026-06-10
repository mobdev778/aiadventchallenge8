package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import org.koin.core.annotation.Single

@Single
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
