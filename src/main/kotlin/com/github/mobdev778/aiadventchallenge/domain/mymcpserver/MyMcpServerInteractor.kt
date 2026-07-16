package com.github.mobdev778.aiadventchallenge.domain.mymcpserver

import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.git.MyMcpGitServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerState
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.project.MyMcpProjectServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag.MyMcpRagSearchServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.ragchat.MyMcpRagChatServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Интерактор управления локальными MCP-серверами.
 *
 * Агрегирует все доступные локальные серверы ([MyMcpServer]) и предоставляет единые потоки
 * состояний, а также методы для запуска и остановки серверов по имени.
 *
 * В состав серверов включены:
 * - [MyMcpReadFileServer] – чтение файлов проекта.
 * - [MyMcpServerSaveToFileServer] – сохранение Markdown в файл.
 * - [ragServer] – семантический поиск по сообщениям (векторный RAG).
 * - [ragChatServer] – семантический поиск в чатах с верификацией источников.
 *
 * @property ragServer Экземпляр сервера общего векторного поиска по сообщениям.
 * @property ragChatServer Экземпляр сервера поиска по чатам с верификацией источников.
 */
@Single
class MyMcpServerInteractor(
    private val ragServer: MyMcpRagSearchServer,
    private val ragChatServer: MyMcpRagChatServer,
    private val gitServer: MyMcpGitServer,
    private val projectServer: MyMcpProjectServer,
) {

    private val servers: List<MyMcpServer> = listOf(
        MyMcpReadFileServer(),
        MyMcpServerSaveToFileServer(),
        ragServer,
        ragChatServer,
        gitServer,
        projectServer,
    )

    /**
     * Поток, предоставляющий список всех локальных MCP-серверов в виде DTO [McpServer].
     *
     * Для каждого сервера состояние преобразуется в модель с уникальным идентификатором
     * (на основе URL) и флагом `isLocal = true`. Поток комбинирует последние состояния
     * всех серверов и эмитит новый список при изменении любого из них.
     */
    val localServersFlow: Flow<List<McpServer>> = combine(
        servers.map {
            it.observeState()
        }
    ) {
        it.map { server ->
            McpServer(
                id = UUID.nameUUIDFromBytes(server.url.toByteArray(StandardCharsets.UTF_8)),
                active = server.isRunning,
                name = server.name,
                url = server.url,
                isLocal =  true,
            )
        }.toList()
    }

    /**
     * Поток текущих состояний всех локальных MCP-серверов в исходном формате [MyMcpServerState].
     *
     * Эмитит список состояний каждый раз, когда изменяется хотя бы одно из них.
     */
    val serverStatesFlow: Flow<List<MyMcpServerState>> = combine(
        servers.map { it.observeState() }
    ) {
        it.toList()
    }

    /**
     * Запускает сервер с указанным именем.
     *
     * Если сервер не найден, вызов игнорируется. Метод приостанавливается до окончания
     * инициализации сервера.
     *
     * @param name Имя сервера, совпадающее с полем [MyMcpServerState.name] на момент поиска.
     */
    suspend fun start(name: String) {
        val server = servers.firstOrNull { it.observeState().first().name == name }
        server?.start()
    }

    /**
     * Останавливает сервер с указанным именем.
     *
     * Если сервер не найден, вызов игнорируется. Метод приостанавливается до полной
     * остановки всех ресурсов сервера.
     *
     * @param name Имя сервера, совпадающее с полем [MyMcpServerState.name] на момент поиска.
     */
    suspend fun stop(name: String) {
        val server = servers.firstOrNull { it.observeState().first().name == name }
        server?.stop()
    }
}
