package com.github.mobdev778.aiadventchallenge.presentation.app

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.koin.java.KoinJavaComponent.inject

/**
 * Сервис уровня приложения, отвечающий за корректное завершение работы плагина.
 *
 * Реализует [Disposable], чтобы при выгрузке плагина или закрытии IDE
 * централизованно отменить глобальный [CoroutineScope], полученный из Koin-контейнера.
 * Это предотвращает утечки ресурсов и гарантирует, что все запущенные корутины
 * будут остановлены.
 */
@Service(Service.Level.APP)
class PluginShutdownHandlerService : Disposable {

    /**
     * Вызывается IntelliJ Platform при завершении работы сервиса.
     * Получает [CoroutineScope] из Koin-контейнера и отменяет его,
     * инициируя остановку всех корутин, связанных с жизненным циклом плагина.
     */
    override fun dispose() {
        val scope: CoroutineScope by inject(CoroutineScope::class.java)
        scope.cancel()
    }
}
