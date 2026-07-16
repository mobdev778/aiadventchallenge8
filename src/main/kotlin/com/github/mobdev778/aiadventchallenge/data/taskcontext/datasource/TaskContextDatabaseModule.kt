package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

/**
 * Модуль Koin, отвечающий за предоставление зависимостей для слоя данных контекста задачи.
 *
 * Определяет компоненты [TaskContextAppDatabase] и [TaskContextDao] в графе зависимостей Koin.
 * База данных размещается в поддиректории "aiadventchallenge" внутри системной директории IntelliJ
 * (получаемой через [PathManager.getSystemDir]), а DAO извлекается из сконфигурированной базы.
 */
@Module
class TaskContextDatabaseModule {

    /**
     * Создаёт и возвращает экземпляр [TaskContextAppDatabase], инициализированный файлом БД
     * по заданному пути.
     *
     * Путь формируется из системной директории IDE, к которой добавляется поддиректория "aiadventchallenge";
     * если она отсутствует, она создаётся. Файл базы данных называется "task_context.db".
     *
     * @return Готовый к использованию объект [TaskContextAppDatabase].
     */
    @Single
    fun provideTaskContextAppDatabase(): TaskContextAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("task_context.db")
        return TaskContextAppDatabase.create(dbFile)
    }

    /**
     * Предоставляет экземпляр [TaskContextDao], полученный из переданной базы данных.
     *
     * @param taskContextAppDatabase Экземпляр [TaskContextAppDatabase], из которого извлекается DAO.
     * @return Объект [TaskContextDao] для выполнения операций над сущностями контекста задачи.
     */
    @Single
    fun provideTaskContextDao(taskContextAppDatabase: TaskContextAppDatabase): TaskContextDao {
        return taskContextAppDatabase.taskContextDao()
    }
}
