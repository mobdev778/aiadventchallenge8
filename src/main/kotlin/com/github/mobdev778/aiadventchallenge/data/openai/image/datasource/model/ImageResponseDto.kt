package com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model

import kotlinx.serialization.Serializable

@Serializable
class ImageResponseDto(
    val created: Long? = null,
    val data: List<ImageDataDto>,
)