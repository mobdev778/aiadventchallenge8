package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.cosineSimilarity
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.util.PriorityQueue
import kotlin.math.sqrt

private const val SEARCH_PAGE_SIZE = 10
private const val MAX_SEARCH_RESULTS = 3

/**
 * Компонент управления состоянием экрана просмотра конкретного RAG-документа.
 *
 * Отвечает за загрузку информации о документе, обработку пользовательских событий
 * ([ViewRagDocumentScreenEvent]) и выполнение семантического поиска по фрагментам
 * документа с использованием эмбеддингов из [RankerFactory] и косинусного сходства
 * ([cosineSimilarity]).
 *
 * Результаты поиска обновляются в наблюдаемом свойстве [state],
 * а навигационные команды помещаются в [commands].
 *
 * @property ragDocumentRepository репозиторий для доступа к документам и их чанкам
 * @property scope корутин-скоуп, в котором выполняются долгие операции (например, поиск)
 * @property ragRankerFactory фабрика ранкеров, предоставляющая модель эмбеддингов
 */
@Factory
class ViewRagDocumentScreenStateHolder(
    private val ragDocumentRepository: RagDocumentRepository,
    private val scope: CoroutineScope,
    private val ragRankerFactory: RankerFactory,
) {
    private val _state = MutableStateFlow(ViewRagDocumentScreenState())
    /** Текущее состояние экрана, доступное только для чтения. */
    val state: StateFlow<ViewRagDocumentScreenState> = _state.asStateFlow()

    /** Поток команд, отправляемых из UI-логики (например, навигация). */
    val commands = MutableSharedFlow<ViewRagDocumentScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Инициализирует экран для отображения указанного документа.
     * Если документ уже выбран и его идентификатор совпадает, состояние не изменяется.
     *
     * @param document элемент списка RAG-документов, выбранный пользователем
     */
    fun start(document: RagDocumentListItem) {
        _state.update { current ->
            if (current.document?.id == document.id) current else current.copy(document = document)
        }
    }

    /**
     * Обрабатывает пользовательское событие, пришедшее с UI.
     *
     * @param event событие ([ViewRagDocumentScreenEvent]), такое как клик назад,
     * изменение поискового запроса или запуск поиска.
     */
    fun onEvent(event: ViewRagDocumentScreenEvent) {
        when (event) {
            ViewRagDocumentScreenEvent.OnBackClick -> commands.tryEmit(ViewRagDocumentScreenCommand.Back)
            is ViewRagDocumentScreenEvent.OnQueryChange -> {
                _state.update { it.copy(query = event.value) }
            }
            ViewRagDocumentScreenEvent.OnSearchClick -> search()
        }
    }

    private fun search() {
        val snapshot = state.value
        val document = snapshot.document ?: return
        val query = snapshot.query.trim()
        if (query.isEmpty()) return

        scope.launch(Dispatchers.IO) {
            _state.update { it.copy(isSearching = true, results = emptyList()) }

            val queryVector = RagChunkGenerator(document.id, ragRankerFactory.embeddingModel)
                .generate(section = 0, text = query)
                .vector

            val bestChunks = PriorityQueue<Pair<Double, RagDocumentChunk>>(
                compareBy { it.first }
            )
            val pageSize = SEARCH_PAGE_SIZE
            var offset = 0

            while (true) {
                val page = ragDocumentRepository.getChunksPage(
                    documentId = document.id,
                    limit = pageSize,
                    offset = offset,
                )
                if (page.isEmpty()) break

                page.forEach { chunk ->
                    val similarity = queryVector.cosineSimilarity(chunk.vector)
                    bestChunks.offer(similarity to chunk)
                    if (bestChunks.size > MAX_SEARCH_RESULTS) {
                        bestChunks.poll()
                    }
                }

                offset += pageSize
            }

            val results = bestChunks
                .sortedByDescending { it.first }
                .map { (_, chunk) ->
                ViewRagDocumentSearchResult(
                    source = document.source,
                    section = chunk.section,
                    text = chunk.text,
                )
            }

            _state.update { it.copy(isSearching = false, results = results) }
        }
    }
}
