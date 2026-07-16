package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

private const val STATE_FLOW_TIMEOUT_MS = 5000L

/**
 * Основной держатель состояния экрана списка RAG-документов.
 *
 * Отвечает за подготовку и предоставление реактивного UI-состояния ([RagDocumentListScreenState]),
 * а также за обработку пользовательских событий ([RagDocumentListScreenEvent]) и их преобразование
 * в навигационные команды ([RagDocumentListScreenCommand]) или бизнес-действия.
 *
 * Комбинирует данные из двух источников:
 * - [RagConfigRepository] – текущая RAG-конфигурация,
 * - [RagDocumentRepository] – список документов с количеством чанков.
 *
 * Поток команд реализован через [MutableSharedFlow] с буфером единичной ёмкости,
 * что гарантирует доставку последней команды даже при медленной подписке.
 * Вся ресурсоёмкая работа (получение количества чанков, обновление конфигурации,
 * удаление документа) вынесена в фоновую корутину на [Dispatchers.IO].
 *
 * @property ragDocumentRepository Репозиторий для операций с RAG-документами и чанками.
 * @property ragConfigRepository Репозиторий для доступа к RAG-конфигурации.
 * @property scope CoroutineScope, в котором выполняются длительные операции.
 */
@Single
class RagDocumentListScreenStateHolder(
    private val ragDocumentRepository: RagDocumentRepository,
    private val ragConfigRepository: RagConfigRepository,
    private val scope: CoroutineScope,
) {

    /**
     * Поток документов, преобразованный из сущностей базы данных в элементы UI-списка.
     *
     * Для каждого документа вычисляется актуальное количество чанков вызовом
     * [RagDocumentRepository.getChunkCount] на фоне ([Dispatchers.IO]).
     */
    private val documentsFlow = ragDocumentRepository
        .observeDocuments()
        .map { documents ->
            documents.map { document ->
                RagDocumentListItem(
                    id = document.id,
                    source = document.source,
                    title = document.title,
                    chunkCount = ragDocumentRepository.getChunkCount(document.id),
                )
            }
        }
        .flowOn(Dispatchers.IO)

    /**
     * Состояние экрана списка документов RAG, доступное для реактивного наблюдения UI.
     *
     * Формируется путём комбинации последнего значения RAG-конфигурации из [RagConfigRepository]
     * и актуального списка документов. При отсутствии подписчиков в течение [STATE_FLOW_TIMEOUT_MS]
     * миллисекунд сохраняет последнее известное состояние и освобождает ресурсы.
     * В качестве начального значения используется конфигурация по умолчанию и пустой список документов.
     */
    val uiState: StateFlow<RagDocumentListScreenState> = combine(
        ragConfigRepository.observeConfig(),
        documentsFlow,
    ) { ragConfig: RagConfig, documents: List<RagDocumentListItem> ->
        RagDocumentListScreenState(
            ragConfig = ragConfig,
            documents = documents,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS),
        initialValue = RagDocumentListScreenState(
            ragConfig = RagConfigRepository.default,
            documents = emptyList(),
        ),
    )

    /**
     * Поток команд навигации и однократных действий, инициированных пользователем.
     *
     * Использует [MutableSharedFlow] с дополнительным буфером (1), чтобы последняя команда
     * была доставлена даже при отсутствии активной подписки в момент отправки.
     * Подписчиком обычно выступает UI-слой, маршрутизирующий команды к целевым экранам.
     */
    val commands = MutableSharedFlow<RagDocumentListScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Обрабатывает пользовательское событие с экрана списка RAG-документов.
     *
     * В зависимости от типа события:
     * - [RagDocumentListScreenEvent.OnBackClick] – эмиттит команду [RagDocumentListScreenCommand.Back].
     * - [RagDocumentListScreenEvent.OnAddClick] – эмиттит команду [RagDocumentListScreenCommand.OpenAddDocument].
     * - [RagDocumentListScreenEvent.OnRagConfigClick] – эмиттит команду [RagDocumentListScreenCommand.OpenRagConfig].
     * - [RagDocumentListScreenEvent.OnDocumentClick] – эмиттит команду [RagDocumentListScreenCommand.OpenDocument]
     *   с соответствующим элементом документа.
     * - [RagDocumentListScreenEvent.OnDeleteClick] – инициирует фоновое удаление документа
     *   через [deleteDocument].
     *
     * @param event событие экрана, требующее реакции.
     */
    fun onEvent(event: RagDocumentListScreenEvent) {
        when (event) {
            RagDocumentListScreenEvent.OnBackClick -> commands.tryEmit(RagDocumentListScreenCommand.Back)
            RagDocumentListScreenEvent.OnAddClick -> commands.tryEmit(RagDocumentListScreenCommand.OpenAddDocument)
            RagDocumentListScreenEvent.OnRagConfigClick -> commands.tryEmit(RagDocumentListScreenCommand.OpenRagConfig)
            is RagDocumentListScreenEvent.OnDocumentClick -> {
                commands.tryEmit(RagDocumentListScreenCommand.OpenDocument(event.document))
            }
            is RagDocumentListScreenEvent.OnDeleteClick -> deleteDocument(event.documentId)
        }
    }

    /**
     * Применяет переданную функцию трансформации к текущей RAG-конфигурации
     * и сохраняет результат в репозитории. Выполняется в фоновом потоке [Dispatchers.IO].
     *
     * @param transform лямбда-выражение, принимающее текущий [RagConfig] и возвращающее изменённый.
     */
    private fun updateConfig(transform: RagConfig.() -> RagConfig) {
        scope.launch(Dispatchers.IO) {
            val updatedConfig = ragConfigRepository.getConfig().transform()
            ragConfigRepository.updateConfig(updatedConfig)
        }
    }

    /**
     * Удаляет документ с указанным идентификатором через репозиторий.
     * Операция выполняется асинхронно на [Dispatchers.IO].
     *
     * @param documentId уникальный идентификатор документа ([UUID]), подлежащего удалению.
     */
    private fun deleteDocument(documentId: UUID) {
        scope.launch(Dispatchers.IO) {
            ragDocumentRepository.deleteDocument(documentId)
        }
    }
}
