package com.github.mobdev778.aiadventchallenge.domain.openai.image.model

data class ImageResponse(
    val created: Long? = null,
    val data: List<ImageData>,
)