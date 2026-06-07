package com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ImageDataDto(
    @SerialName("b64_json")
    val b64Json: String? = null,
    val url: String? = null,
) {
}