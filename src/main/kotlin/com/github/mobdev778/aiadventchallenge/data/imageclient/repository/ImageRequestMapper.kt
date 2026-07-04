package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageRequestDto
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageRequest

class ImageRequestMapper {

    fun map(request: ImageRequest): ImageRequestDto {
        return ImageRequestDto(
            model = request.model,
            prompt = request.prompt,
            size = request.size,
        )
    }
}
