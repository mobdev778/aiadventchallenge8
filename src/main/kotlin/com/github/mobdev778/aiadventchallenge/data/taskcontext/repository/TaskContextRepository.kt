package com.github.mobdev778.aiadventchallenge.data.taskcontext.repository

import com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.TaskContextDao
import com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.model.TaskContextEntity
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Репозиторий для управления контекстом выполнения задачи.
 *
 * Отвечает за преобразование данных между слоем доступа к данным ([TaskContextDao])
 * и доменным слоем ([TaskContext]). Инкапсулирует логику локального хранения,
 * сериализации и десериализации полей плана и выполненных шагов, а также
 * предоставляет реактивное наблюдение за изменениями контекста посредством [Flow].
 *
 * Является центральной точкой взаимодействия с контекстом задачи на уровне данных
 * и используется доменными или презентационными слоями через инверсию зависимостей.
 */
@Single
class TaskContextRepository(
    private val taskContextDao: TaskContextDao,
) {

    /**
     * Возвращает поток ([Flow]) объекта [TaskContext], отражающий актуальный контекст задачи.
     *
     * При каждом изменении соответствующей записи в базе данных подписчик получает обновлённое
     * значение. Повторяющиеся элементы отфильтровываются с помощью [distinctUntilChanged].
     *
     * @param id Уникальный идентификатор контекста задачи.
     * @return Поток [TaskContext], эмитирующий текущий контекст или `null`, если запись отсутствует.
     */
    fun observeTaskContext(id: UUID): Flow<TaskContext?> =
        taskContextDao.observe(id)
            .map { it?.toDomain() }
            .distinctUntilChanged()

    /**
     * Асинхронно получает текущий контекст задачи по идентификатору.
     *
     * Выполняет однократное чтение из базы данных и преобразует сущность в доменный объект.
     *
     * @param id Уникальный идентификатор контекста задачи.
     * @return Доменный объект [TaskContext] или `null`, если запись не найдена.
     */
    suspend fun getTaskContext(id: UUID): TaskContext? = taskContextDao.get(id)?.toDomain()

    /**
     * Сохраняет (вставляет или обновляет) контекст задачи в базе данных.
     *
     * Преобразует доменный объект [TaskContext] в сущность [TaskContextEntity] и выполняет upsert
     * через [TaskContextDao.upsert]. При конфликте по первичному ключу существующая запись заменяется.
     *
     * @param taskContext Объект контекста задачи для сохранения.
     */
    suspend fun saveTaskContext(taskContext: TaskContext) {
        taskContextDao.upsert(taskContext.toEntity())
    }

    /**
     * Удаляет запись контекста задачи по её идентификатору.
     *
     * @param id Уникальный идентификатор контекста, подлежащего удалению.
     */
    suspend fun deleteTaskContext(id: UUID) {
        taskContextDao.delete(id)
    }

    /**
     * Удаляет все записи контекстов задач из таблицы.
     */
    suspend fun clearTaskContext() {
        taskContextDao.clear()
    }

    private fun TaskContextEntity.toDomain(): TaskContext =
        TaskContext(
            id = id,
            task = task,
            state = TaskState.valueOf(state),
            step = step,
            plan = decodeList(plan),
            done = decodeList(done),
            current = current,
        )

    private fun TaskContext.toEntity(): TaskContextEntity =
        TaskContextEntity(
            id = id,
            task = task,
            state = state.name,
            step = step,
            plan = encodeList(plan),
            done = encodeList(done),
            current = current,
        )

    private fun encodeList(list: List<String>): String = list.joinToString("\n")

    private fun decodeList(text: String): List<String> =
        text
            .split("\n")
            .map { it.trimEnd() }
            .filter { it.isNotBlank() }
}
