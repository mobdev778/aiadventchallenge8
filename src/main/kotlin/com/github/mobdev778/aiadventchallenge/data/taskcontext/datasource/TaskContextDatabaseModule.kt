package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

@Module
class TaskContextDatabaseModule {

    @Single
    fun provideTaskContextAppDatabase(): TaskContextAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("task_context.db")
        return TaskContextAppDatabase.create(dbFile)
    }

    @Single
    fun provideTaskContextDao(taskContextAppDatabase: TaskContextAppDatabase): TaskContextDao {
        return taskContextAppDatabase.taskContextDao()
    }
}
