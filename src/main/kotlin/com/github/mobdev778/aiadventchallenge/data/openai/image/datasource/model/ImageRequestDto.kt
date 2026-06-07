package com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model

import kotlinx.serialization.Serializable

@Serializable
class ImageRequestDto(
    val model: String,
    val prompt: String,
    val size: String,
) {

}