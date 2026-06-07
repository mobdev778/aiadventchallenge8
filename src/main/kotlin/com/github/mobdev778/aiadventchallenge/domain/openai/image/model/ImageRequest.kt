package com.github.mobdev778.aiadventchallenge.domain.openai.image.model

data class ImageRequest(
    val model: String,
    val prompt: String,
    val size: String,
)