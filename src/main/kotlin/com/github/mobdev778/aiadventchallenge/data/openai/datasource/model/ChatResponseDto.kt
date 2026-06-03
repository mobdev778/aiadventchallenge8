package com.github.mobdev778.aiadventchallenge.data.openai.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponseDto(
    val choices: List<ChoiceDto>
)