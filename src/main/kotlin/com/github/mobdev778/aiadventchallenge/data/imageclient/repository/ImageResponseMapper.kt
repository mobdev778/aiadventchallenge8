package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageResponseDto
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageResponse

/**
 * Маппер, преобразующий объекты сетевого DTO ответа с изображениями ([ImageResponseDto])
 * в сущности доменного слоя ([ImageResponse]).
 *
 * Основная задача класса — инкапсуляция логики трансформации ответа удалённого API,
 * включая маппинг вложенных данных изображений через [ImageDataMapper],
 * для изоляции слоёв приложения.
 *
 * @property imageDataMapper Маппер для преобразования отдельных данных изображений.
 */
class ImageResponseMapper(
    private val imageDataMapper: ImageDataMapper,
) {

    /**
     * Преобразует DTO ответа с изображениями в доменную модель [ImageResponse].
     *
     * Выполняет перенос временной метки [ImageResponseDto.created] и
     * последовательное преобразование списка [ImageResponseDto.data]
     * с использованием [imageDataMapper].
     *
     * @param responseDto DTO-объект ответа от удалённого API.
     * @return Доменная модель ответа, готовая к использованию в бизнес-логике.
     */
    fun map(responseDto: ImageResponseDto): ImageResponse {
        return ImageResponse(
            created = responseDto.created,
            data = responseDto.data.map {
                imageDataMapper.map(it)
            },
        )
    }
}
