package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.model

import java.util.UUID

/**
 * Состояние экрана редактирования профиля.
 *
 * Представляет собой иммутабельный снимок всех данных, необходимых для отображения
 * и редактирования профиля на соответствующем экране. Используется в связке
 * с ViewModel для реализации однонаправленного потока данных (UDF).
 *
 * @property profileId Уникальный идентификатор профиля. `null`, если создаётся новый профиль.
 * @property name Отображаемое имя профиля. Пустая строка по умолчанию.
 * @property content Содержимое профиля (описание, заметки или иные данные). Пустая строка по умолчанию.
 */
data class EditProfileScreenState(
    val profileId: UUID? = null,
    val name: String = "",
    val content: String = "",
)
