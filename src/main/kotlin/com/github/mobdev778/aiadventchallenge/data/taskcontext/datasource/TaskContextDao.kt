package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.model.TaskContextEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TaskContextDao {

    @Query("SELECT * FROM task_context WHERE id = :id LIMIT 1")
    fun observe(id: UUID): Flow<TaskContextEntity?>

    @Query("SELECT * FROM task_context WHERE id = :id LIMIT 1")
    suspend fun get(id: UUID): TaskContextEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TaskContextEntity)

    @Query("DELETE FROM task_context WHERE id = :id")
    suspend fun delete(id: UUID)

    @Query("DELETE FROM task_context")
    suspend fun clear()
}
