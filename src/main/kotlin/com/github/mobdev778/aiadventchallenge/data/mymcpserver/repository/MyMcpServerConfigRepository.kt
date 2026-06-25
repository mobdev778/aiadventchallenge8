package com.github.mobdev778.aiadventchallenge.data.mymcpserver.repository

import com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource.MyMcpServerConfigDao
import com.github.mobdev778.aiadventchallenge.data.mymcpserver.datasource.model.MyMcpServerConfigEntity
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class MyMcpServerConfigRepository(
    private val dao: MyMcpServerConfigDao,
) {

    fun observeConfig(): Flow<MyMcpServerConfig> =
        dao.observe()
            .map { entity -> entity?.toDomain() ?: default }
            .distinctUntilChanged()

    suspend fun save(config: MyMcpServerConfig) {
        dao.upsert(config.toEntity())
    }

    companion object {
        val default = MyMcpServerConfig(
            launchAtStartup = false,
            port = 3000,
        )
    }

    private fun MyMcpServerConfigEntity.toDomain(): MyMcpServerConfig =
        MyMcpServerConfig(
            launchAtStartup = launchAtStartup,
            port = port,
        )

    private fun MyMcpServerConfig.toEntity(): MyMcpServerConfigEntity =
        MyMcpServerConfigEntity(
            launchAtStartup = launchAtStartup,
            port = port,
        )
}
