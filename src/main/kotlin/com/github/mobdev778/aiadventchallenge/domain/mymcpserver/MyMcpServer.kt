package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerState
import kotlinx.coroutines.flow.Flow

interface MyMcpServer {

    fun observeState(): Flow<MyMcpServerState>

    suspend fun start()

    suspend fun stop()
}
