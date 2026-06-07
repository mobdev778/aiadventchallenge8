package com.github.mobdev778.aiadventchallenge.data.openai.image.repository

import com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.ImageRestApi
import com.github.mobdev778.aiadventchallenge.domain.openai.image.model.ImageRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.image.model.ImageResponse

class ImageRepository(
    private val restApi: ImageRestApi,
    private val requestMapper: ImageRequestMapper,
    private val responseMapper: ImageResponseMapper,
) {

    suspend fun imagesGenerations(
        request: ImageRequest,
    ): ImageResponse {
        val requestDto = requestMapper.map(request)
        val responseDto = restApi.imagesGenerations(requestDto)
        return responseMapper.map(responseDto)
    }
}