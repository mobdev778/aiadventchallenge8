package com.github.mobdev778.aiadventchallenge.data.openai.image.repository

import com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model.ImageDataDto
import com.github.mobdev778.aiadventchallenge.domain.openai.image.model.ImageData

class ImageDataMapper {

    fun map(dto: ImageDataDto): ImageData {
        return ImageData(
            url = dto.url,
            b64Json = dto.b64Json,
        )
    }
}