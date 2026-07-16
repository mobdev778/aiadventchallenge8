package com.github.mobdev778.aiadventchallenge.presentation.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Модуль Koin, регистрирующий синглтонные зависимости уровня приложения.
 *
 * Предоставляет единый [CoroutineScope] для запуска корутин в рамках жизненного цикла приложения
 * и контейнер [ProjectContainer] для доступа к текущему проекту IntelliJ IDEA.
 */
@Module
class AppModule {

    /**
     * Создаёт корневой [CoroutineScope] приложения.
     *
     * Использует [SupervisorJob] для изоляции ошибок дочерних корутин и [Dispatchers.Main]
     * в качестве основного диспетчера. Гарантирует, что все компоненты, работающие с этим скоупом,
     * будут выполнять UI-операции на главном потоке.
     *
     * @return экземпляр [CoroutineScope], готовый к использованию в рамках приложения.
     */
    @Single
    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * Предоставляет синглтон [ProjectContainer], хранящий ссылку на текущий проект.
     *
     * [ProjectContainer] является единой точкой доступа к проекту IntelliJ IDEA внутри плагина.
     * При отсутствии активного проекта (например, стартовый экран IDE) его поле будет `null`.
     *
     * @return экземпляр [ProjectContainer], зарегистрированный в контейнере внедрения зависимостей.
     */
    @Single
    fun provideProjectContainer(): ProjectContainer = ProjectContainer()
}
