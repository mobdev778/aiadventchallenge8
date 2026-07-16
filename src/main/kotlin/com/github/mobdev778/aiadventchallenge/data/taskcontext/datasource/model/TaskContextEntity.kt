package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Сущность базы данных Room, представляющая контекст выполнения задачи.
 *
 * Хранит полную информацию о текущем состоянии задачи в таблице `task_context`,
 * включая описание самой задачи, её статус, текущий шаг выполнения, общий план,
 * список завершённых этапов и текущий выполняемый элемент. Используется в слое
 * данных как основная модель для локального хранения и восстановления контекста
 * работы AI-агента.
 *
 * @property id Уникальный идентификатор записи контекста задачи.
 * @property task Текстовое описание поставленной задачи.
 * @property state Текущее состояние выполнения задачи (например, "в процессе", "завершено", "ошибка").
 * @property step Порядковый номер текущего шага выполнения.
 * @property plan Общий план действий по выполнению задачи в структурированном виде.
 * @property done Перечень уже завершённых этапов или действий.
 * @property current Описание текущего выполняемого этапа или действия.
 */
@Entity(tableName = "task_context")
data class TaskContextEntity(
    @PrimaryKey
    val id: UUID,

    @ColumnInfo(name = "task")
    val task: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "step")
    val step: Int,

    @ColumnInfo(name = "plan")
    val plan: String,

    @ColumnInfo(name = "done")
    val done: String,

    @ColumnInfo(name = "current")
    val current: String,
)
