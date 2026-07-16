package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.model

import androidx.compose.runtime.Immutable

/**
 * Состояние экрана добавления профиля.
 *
 * Хранит все данные, необходимые для отображения и взаимодействия с формой
 * создания нового профиля. Используется в архитектурном паттерне
 * Model-View-Intent (MVI) как иммутабельный контейнер состояния,
 * что позволяет Compose-компилятору эффективно отслеживать изменения
 * и запускать рекомпозицию только при необходимости.
 *
 * @property name Имя профиля, вводимое пользователем. По умолчанию пустая строка.
 * @property content Содержимое профиля — основное текстовое описание или
 *                  контент, связанный с профилем. По умолчанию пустая строка.
 */
@Immutable
data class AddProfileScreenState(
    val name: String = "",
    val content: String = "",
)
