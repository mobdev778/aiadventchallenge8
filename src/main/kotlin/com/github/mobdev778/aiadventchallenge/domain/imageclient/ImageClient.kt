package com.github.mobdev778.aiadventchallenge.domain.imageclient

import com.github.mobdev778.aiadventchallenge.data.imageclient.repository.ImageRepository
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageRequest
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageResponse
import org.koin.core.annotation.Single

/**
 * Клиент для выполнения запросов на генерацию изображений.
 *
 * Предоставляет высокоуровневый интерфейс к функциональности генерации изображений,
 * делегируя все вызовы [ImageRepository]. Является центральной точкой входа
 * для выполнения запросов на стороне предметной области.
 *
 * @property repository репозиторий, используемый для выполнения запросов к API генерации изображений.
 */
@Single
class ImageClient(
    private val repository: ImageRepository,
) {

    /**
     * Выполняет запрос на генерацию изображения.
     *
     * @param request запрос, содержащий параметры генерации (модель, промпт, размер).
     * @return [ImageResponse] с результатом, включая список сгенерированных изображений и временную метку создания.
     */
    suspend fun execute(request: ImageRequest): ImageResponse {
        return repository.imagesGenerations(request)
    }
}
