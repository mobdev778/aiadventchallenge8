package com.github.mobdev778.aiadventchallenge.domain.openai.image

import com.github.mobdev778.aiadventchallenge.data.openai.image.repository.ImageRepository
import com.github.mobdev778.aiadventchallenge.domain.openai.image.model.ImageRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.image.model.ImageResponse

class ImageClient(
    private val repository: ImageRepository,
) {

    suspend fun execute(request: ImageRequest): ImageResponse {
        return repository.imagesGenerations(request)
    }
}