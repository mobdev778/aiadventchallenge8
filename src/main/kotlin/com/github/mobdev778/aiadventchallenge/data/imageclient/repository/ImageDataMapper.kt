package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageDataDto
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageData

class ImageDataMapper {

    fun map(dto: ImageDataDto): ImageData {
        return ImageData(
            url = dto.url,
            b64Json = dto.b64Json,
        )
    }
}
