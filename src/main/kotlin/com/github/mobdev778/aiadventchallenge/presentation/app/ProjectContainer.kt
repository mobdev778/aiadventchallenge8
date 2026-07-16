package com.github.mobdev778.aiadventchallenge.presentation.app

import com.intellij.openapi.project.Project
import org.koin.core.annotation.Single

/**
 * Контейнер для хранения ссылки на текущий [Project] IntelliJ IDEA.
 *
 * Этот класс зарегистрирован как синглтон в контексте внедрения зависимостей Koin
 * (благодаря аннотации [Single]) и служит единой точкой доступа к проекту
 * для всех компонентов плагина, которым необходимо взаимодействовать
 * с API IntelliJ Platform.
 */
@Single
class ProjectContainer {

    /**
     * Текущий проект IntelliJ IDEA, в контексте которого работает плагин.
     *
     * Значение `null` означает, что проект ещё не был установлен или контекст
     * проекта недоступен (например, пользователь находится на стартовом экране IDE).
     */
    var project: Project? = null
}
