package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageResponseDto
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageResponse

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