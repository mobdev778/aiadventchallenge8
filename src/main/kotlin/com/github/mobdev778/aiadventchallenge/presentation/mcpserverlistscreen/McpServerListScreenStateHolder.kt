package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Холдер состояния экрана списка MCP-серверов. Является центральным узлом управления
 * состоянием и побочными эффектами для данного экрана. Объединяет потоки данных
 * о серверах, получаемые через [McpServerInteractor], и предоставляет командный
 * интерфейс для навигации, следуя архитектурному паттерну UDF (Unidirectional Data Flow).
 *
 * @param mcpServerInteractor Интерактор доменного слоя, отвечающий за бизнес-логику работы с MCP-серверами.
 * @param scope CoroutineScope, в котором выполняются все асинхронные операции данного холдера.
 */
@Single
class McpServerListScreenStateHolder(
    private val mcpServerInteractor: McpServerInteractor,
    private val scope: CoroutineScope,
) {

    /**
     * Поток состояния списка всех MCP-серверов. Данные извлекаются из [McpServerInteractor.allServersFlow]
     * с переключением на [Dispatchers.IO] и преобразуются в горячий StateFlow с политикой
     * ожидания подписчиков [SharingStarted.WhileSubscribed] с таймаутом [SUBSCRIPTION_TIMEOUT] мс.
     */
    val servers: StateFlow<List<McpServer>> = mcpServerInteractor
        .allServersFlow
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT),
            emptyList()
        )

    /**
     * Поток команд навигации и действий, инициируемых пользователем на экране.
     * Использует [MutableSharedFlow] с буфером ёмкостью 1 для доставки команд
     * подписчикам (обычно UI-контроллеру) без строгой гарантии доставки,
     * но допускающим потерю событий, если потребитель не готов.
     */
    val commands = MutableSharedFlow<McpServerListScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Обрабатывает входящие события от UI-слоя. В зависимости от типа события
     * либо эмитирует команду в [commands], либо выполняет асинхронную операцию
     * через [McpServerInteractor] (например, изменение активности или удаление сервера).
     *
     * @param event Событие пользовательского интерфейса, см. [McpServerListScreenEvent].
     */
    fun onEvent(event: McpServerListScreenEvent) {
        when (event) {
            McpServerListScreenEvent.OnBackClick -> {
                commands.tryEmit(McpServerListScreenCommand.Back)
            }
            McpServerListScreenEvent.OnAddClick -> {
                commands.tryEmit(McpServerListScreenCommand.OpenAddServer)
            }
            is McpServerListScreenEvent.OnActiveChanged -> {
                updateActive(event.serverId, event.active)
            }
            is McpServerListScreenEvent.OnServerClick -> {
                commands.tryEmit(McpServerListScreenCommand.OpenServerInfo(event.serverId))
            }
            is McpServerListScreenEvent.OnDeleteClick -> {
                deleteServer(event.serverId)
            }
        }
    }

    /**
     * Асинхронно обновляет состояние активности сервера, делегируя вызов
     * в [McpServerInteractor.updateServerActive] на фоне ([Dispatchers.IO]).
     *
     * @param serverId Идентификатор сервера.
     * @param active Новое состояние активности.
     */
    private fun updateActive(serverId: UUID, active: Boolean) {
        scope.launch(Dispatchers.IO) {
            mcpServerInteractor.updateServerActive(serverId = serverId, active = active)
        }
    }

    /**
     * Асинхронно удаляет сервер из системы, делегируя вызов
     * в [McpServerInteractor.deleteServer] на фоне ([Dispatchers.IO]).
     *
     * @param serverId Идентификатор сервера, подлежащего удалению.
     */
    private fun deleteServer(serverId: UUID) {
        scope.launch(Dispatchers.IO) {
            mcpServerInteractor.deleteServer(serverId = serverId)
        }
    }
}

/**
 * Таймаут ожидания (в миллисекундах) при использовании стратегии [SharingStarted.WhileSubscribed].
 * Определяет, как долго upstream-поток остаётся активным после потери последнего подписчика.
 */
const val SUBSCRIPTION_TIMEOUT = 5000L
