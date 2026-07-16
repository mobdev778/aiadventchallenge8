/**
 * Файл настроек проекта для системы сборки Gradle с использованием Kotlin DSL.
 *
 * Определяет используемые репозитории плагинов и задаёт имя корневого проекта.
 *
 * @see pluginManagement — блок конфигурации репозиториев, из которых Gradle будет загружать плагины.
 * @see rootProject.name — устанавливает имя корневого проекта, используемое в дальнейшем при сборке.
 */
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "AIAdventChallenge8"
