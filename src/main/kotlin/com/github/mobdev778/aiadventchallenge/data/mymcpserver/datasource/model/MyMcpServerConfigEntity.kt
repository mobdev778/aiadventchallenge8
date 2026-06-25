package com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_mcp_server_config")
data class MyMcpServerConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val launchAtStartup: Boolean,
    val port: Int,
)
