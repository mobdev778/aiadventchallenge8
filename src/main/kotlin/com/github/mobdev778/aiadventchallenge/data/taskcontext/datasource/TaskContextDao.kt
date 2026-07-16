package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.model.TaskContextEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DAO-интерфейс для работы с [TaskContextEntity] в локальной базе данных Room.
 *
 * Предоставляет методы для наблюдения, получения, вставки/обновления и удаления записей контекста задачи.
 * Используется источником данных слоя `taskcontext` для выполнения операций над таблицей `task_context`.
 */
@Dao
interface TaskContextDao {

    /**
     * Возвращает поток ([Flow]) сущности контекста задачи по её идентификатору.
     *
     * При любом изменении записи в базе данных подписчик получает актуальное значение.
     * Если запись не существует, возвращается `null`.
     *
     * @param id Уникальный идентификатор контекста задачи ([UUID]).
     * @return Поток, эмитирующий [TaskContextEntity] или `null`.
     */
    @Query("SELECT * FROM task_context WHERE id = :id LIMIT 1")
    fun observe(id: UUID): Flow<TaskContextEntity?>

    /**
     * Асинхронно получает сущность контекста задачи по её идентификатору.
     *
     * Выполняет однократное чтение из базы данных и возвращает результат или `null`,
     * если запись отсутствует.
     *
     * @param id Уникальный идентификатор контекста задачи.
     * @return Сущность [TaskContextEntity] или `null`.
     */
    @Query("SELECT * FROM task_context WHERE id = :id LIMIT 1")
    suspend fun get(id: UUID): TaskContextEntity?

    /**
     * Вставляет новую или заменяет существующую запись контекста задачи.
     *
     * Использует стратегию [OnConflictStrategy.REPLACE]: при конфликте первичного ключа
     * старая запись полностью заменяется новой.
     *
     * @param entity Сохраняемая сущность [TaskContextEntity].
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TaskContextEntity)

    /**
     * Удаляет запись контекста задачи по её идентификатору.
     *
     * @param id Уникальный идентификатор контекста задачи, подлежащей удалению.
     */
    @Query("DELETE FROM task_context WHERE id = :id")
    suspend fun delete(id: UUID)

    /**
     * Удаляет все записи контекста задач из таблицы `task_context`.
     */
    @Query("DELETE FROM task_context")
    suspend fun clear()
}
