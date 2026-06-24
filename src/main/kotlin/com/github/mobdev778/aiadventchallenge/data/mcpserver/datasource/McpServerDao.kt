package com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource.model.McpServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface McpServerDao {

    @Query("SELECT * FROM mcp_servers ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<McpServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: McpServerEntity)

    @Query("UPDATE mcp_servers SET active = :active WHERE id = :id")
    suspend fun updateActive(id: String, active: Boolean)
}
