package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model.ImageDataDto
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageData

/**
 * Маппер, преобразующий DTO-модель изображения [ImageDataDto], полученную от удалённого API,
 * в доменную модель [ImageData], используемую в предметной области.
 *
 * Отвечает за изоляцию слоя данных от деталей внешнего представления и гарантирует,
 * что остальные компоненты системы работают с единым форматом данных изображения.
 */
class ImageDataMapper {

    /**
     * Преобразует переданный DTO-объект в модель предметной области.
     *
     * @param dto объект [ImageDataDto], содержащий изображение в виде Base64-строки или URL.
     * @return экземпляр [ImageData] с соответствующими полями [ImageData.url] и [ImageData.b64Json].
     */
    fun map(dto: ImageDataDto): ImageData {
        return ImageData(
            url = dto.url,
            b64Json = dto.b64Json,
        )
    }
}
