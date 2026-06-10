package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.ImageRestApi
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageRequest
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageResponse
import org.koin.core.annotation.Single
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit
import kotlin.getValue

@Single
class ImageRepository(
    private val requestMapper: ImageRequestMapper,
    private val responseMapper: ImageResponseMapper,
) {

    suspend fun imagesGenerations(
        request: ImageRequest,
    ): ImageResponse {
        // настройки доступа к модели могут меняться на ходу, поэтому мы каждый раз строим ретрофит заново
        val retrofit: Retrofit by inject(Retrofit::class.java)
        val restApi: ImageRestApi = retrofit.create(ImageRestApi::class.java)
        val requestDto = requestMapper.map(request)
        val responseDto = restApi.imagesGenerations(requestDto)
        return responseMapper.map(responseDto)
    }
}