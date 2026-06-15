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

@Single
class TaskContextRepository(
    private val taskContextDao: TaskContextDao,
) {

    fun observeTaskContext(id: UUID): Flow<TaskContext?> =
        taskContextDao.observe(id)
            .map { it?.toDomain() }
            .distinctUntilChanged()

    suspend fun getTaskContext(id: UUID): TaskContext? = taskContextDao.get(id)?.toDomain()

    suspend fun saveTaskContext(taskContext: TaskContext) {
        taskContextDao.upsert(taskContext.toEntity())
    }

    suspend fun deleteTaskContext(id: UUID) {
        taskContextDao.delete(id)
    }

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
