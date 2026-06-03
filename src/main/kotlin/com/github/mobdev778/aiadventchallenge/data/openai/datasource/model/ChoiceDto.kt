package com.github.mobdev778.aiadventchallenge.data.openai.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class ChoiceDto(
    val message: MessageDto
)