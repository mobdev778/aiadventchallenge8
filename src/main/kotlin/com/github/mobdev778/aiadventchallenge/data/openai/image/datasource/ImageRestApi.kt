package com.github.mobdev778.aiadventchallenge.data.openai.image.datasource

import com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model.ImageRequestDto
import com.github.mobdev778.aiadventchallenge.data.openai.image.datasource.model.ImageResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageRestApi {

    @POST("images/generations")
    suspend fun imagesGenerations(
        @Body request: ImageRequestDto
    ): ImageResponseDto
}