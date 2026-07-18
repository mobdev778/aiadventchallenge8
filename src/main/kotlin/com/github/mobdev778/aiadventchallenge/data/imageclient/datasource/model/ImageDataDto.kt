package com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO-модель, представляющая данные сгенерированного изображения,
 * полученные от удалённого API.
 *
 * Может содержать либо строку изображения в кодировке Base64, либо
 * общедоступный URL для его получения.
 *
 * @property b64Json строка изображения, закодированная в формате Base64.
 * @property url URL-адрес, по которому доступно изображение.
 */
@Suppress("EmptyClassBlock")
@Serializable
class ImageDataDto(
    @SerialName("b64_json")
    val b64Json: String? = null,
    val url: String? = null,
) {
}
