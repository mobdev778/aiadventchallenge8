package com.github.mobdev778.aiadventchallenge.data.openai.image.repository

import com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model.ImageRequestDto
import com.github.mobdev778.aiadventchallenge.domain.openai.image.model.ImageRequest

class ImageRequestMapper {

    fun map(request: ImageRequest): ImageRequestDto {
        return ImageRequestDto(
            model = request.model,
            prompt = request.prompt,
            size = request.size,
        )
    }
}