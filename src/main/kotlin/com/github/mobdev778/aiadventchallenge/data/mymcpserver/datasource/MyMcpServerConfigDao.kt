package com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource.model.MyMcpServerConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyMcpServerConfigDao {

    @Query("SELECT * FROM my_mcp_server_config WHERE id = 1")
    fun observe(): Flow<MyMcpServerConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MyMcpServerConfigEntity)
}
