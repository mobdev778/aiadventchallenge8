package com.github.mobdev778.aiadventchallenge.domain.imageclient.model

/**
 * Модель данных ответа от клиента изображений, содержащая список изображений
 * и необязательную временную метку создания.
 *
 * Используется в слое предметной области для получения данных изображений
 * от внешних источников (API) и передачи их другим компонентам.
 *
 * @property created Временная метка создания ответа в миллисекундах эпохи Unix. Может быть `null`.
 * @property data Список объектов [ImageData], представляющих полученные изображения.
 */
data class ImageResponse(
    val created: Long? = null,
    val data: List<ImageData>,
)
