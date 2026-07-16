package com.github.mobdev778.aiadventchallenge.data.profile.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

/**
 * Koin-модуль для внедрения зависимостей, связанных с базой данных профилей.
 *
 * Предоставляет централизованные определения синглтонов [ProfileAppDatabase] и [ProfileDao],
 * используя встроенные механизмы IntelliJ Platform для размещения файла базы данных.
 * Все компоненты регистрируются в DI-контейнере Koin как [Single].
 */
@Module
class ProfileDatabaseModule {

    /**
     * Создаёт и возвращает синглтон-экземпляр [ProfileAppDatabase].
     *
     * База данных размещается в системной директории IntelliJ (`PathManager.getSystemDir()`)
     * внутри подкаталога `aiadventchallenge`. При необходимости создаются все отсутствующие
     * родительские папки. Итоговый путь до файла — `.../aiadventchallenge/profiles.db`.
     *
     * @return готовый экземпляр [ProfileAppDatabase], настроенный для работы с локальной БД профилей.
     */
    @Single
    fun provideProfileAppDatabase(): ProfileAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("profiles.db")
        return ProfileAppDatabase.create(dbFile)
    }

    /**
     * Извлекает из переданной базы данных реализацию [ProfileDao] и регистрирует её как синглтон.
     *
     * @param profileAppDatabase экземпляр [ProfileAppDatabase], созданный ранее в контексте модуля.
     * @return объект [ProfileDao], предоставляющий доступ к операциям с сущностями профилей.
     */
    @Single
    fun provideProfileDao(profileAppDatabase: ProfileAppDatabase): ProfileDao {
        return profileAppDatabase.profileDao()
    }
}
