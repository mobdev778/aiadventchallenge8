package com.github.mobdev778.aiadventchallenge.domain.imageclient

import com.github.mobdev778.aiadventchallenge.data.imageclient.repository.ImageRepository
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageRequest
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageResponse
import org.koin.core.annotation.Single

@Single
class ImageClient(
    private val repository: ImageRepository,
) {

    suspend fun execute(request: ImageRequest): ImageResponse {
        return repository.imagesGenerations(request)
    }
}
