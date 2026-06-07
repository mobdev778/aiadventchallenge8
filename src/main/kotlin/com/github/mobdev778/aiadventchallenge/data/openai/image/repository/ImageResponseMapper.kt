package com.github.mobdev778.aiadventchallenge.data.openai.image.repository

import com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model.ImageResponseDto
import com.github.mobdev778.aiadventchallenge.domain.openai.image.model.ImageResponse

class ImageResponseMapper(
    private val imageDataMapper: ImageDataMapper,
) {

    fun map(responseDto: ImageResponseDto): ImageResponse {
        return ImageResponse(
            created = responseDto.created,
            data = responseDto.data.map {
                imageDataMapper.map(it)
            },
        )
    }
}