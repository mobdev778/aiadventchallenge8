package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageRequestDto
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageRequest

/**
 * Маппер для преобразования доменной модели запроса на генерацию изображения
 * в объект Data Transfer Object (DTO), который используется при отправке
 * запросов к внешнему API через слой данных.
 *
 * Отвечает за однозначное отображение полей [ImageRequest] в [ImageRequestDto],
 * обеспечивая чистоту архитектурных слоёв и инкапсуляцию деталей транспортного уровня.
 */
class ImageRequestMapper {

    /**
     * Преобразует доменный объект запроса в транспортный DTO.
     *
     * @param request Доменная модель запроса, содержащая идентификатор модели,
     *                текстовое описание (промпт) и размер изображения.
     * @return DTO-объект, готовый к сериализации и передаче в теле HTTP-запроса.
     */
    fun map(request: ImageRequest): ImageRequestDto {
        return ImageRequestDto(
            model = request.model,
            prompt = request.prompt,
            size = request.size,
        )
    }
}
