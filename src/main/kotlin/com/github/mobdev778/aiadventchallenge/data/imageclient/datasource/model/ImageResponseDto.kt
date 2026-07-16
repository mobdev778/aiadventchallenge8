package com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model

import kotlinx.serialization.Serializable

/**
 * DTO-модель, представляющая ответ удалённого API на запрос генерации изображений.
 *
 * Содержит список сгенерированных изображений ([ImageDataDto]) и необязательную временную метку
 * создания ответа.
 *
 * @property created Unix-временная метка создания ответа в миллисекундах, может быть null.
 * @property data Список DTO-объектов, представляющих сгенерированные изображения.
 */
@Serializable
class ImageResponseDto(
    val created: Long? = null,
    val data: List<ImageDataDto>,
)
