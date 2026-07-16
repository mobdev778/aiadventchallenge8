package com.github.mobdev778.aiadventchallenge.data.profile.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность базы данных Room, представляющая профиль в таблице `profiles`.
 *
 * Используется в качестве модели данных на уровне локального хранилища для
 * сохранения и извлечения информации о профилях пользователей. Каждый профиль
 * содержит уникальный идентификатор, имя, контент и флаг выбора.
 *
 * @property id Уникальный идентификатор профиля. Является первичным ключом таблицы.
 * @property name Отображаемое имя профиля.
 * @property content Содержимое профиля (например, текст описания, настройки или иные данные).
 * @property isSelected Флаг, указывающий, выбран ли данный профиль в качестве активного.
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "is_selected")
    val isSelected: Boolean,
)
