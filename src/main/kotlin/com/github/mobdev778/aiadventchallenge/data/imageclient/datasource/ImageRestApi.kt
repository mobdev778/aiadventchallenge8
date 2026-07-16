package com.github.mobdev778.aiadventchallenge.data.imageclient.datasource

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageRequestDto
import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Интерфейс Retrofit для взаимодействия с удалённым REST API генерации изображений.
 *
 * Определяет эндпоинты для отправки запросов к внешнему сервису генерации изображений.
 * Использует модели данных [ImageRequestDto] и [ImageResponseDto] для сериализации/десериализации
 * тела запроса и ответа соответственно.
 */
interface ImageRestApi {

    /**
     * Отправляет POST-запрос на эндпоинт `images/generations` для генерации изображений.
     *
     * Приостанавливаемая функция, выполняющая асинхронный HTTP-запрос к удалённому API.
     *
     * @param request DTO-объект, содержащий параметры генерации: модель ИИ, текстовое описание (промпт) и размер.
     * @return [ImageResponseDto] — десериализованный ответ API, содержащий список сгенерированных изображений
     *         и, возможно, временную метку создания.
     */
    @POST("images/generations")
    suspend fun imagesGenerations(
        @Body request: ImageRequestDto
    ): ImageResponseDto
}
