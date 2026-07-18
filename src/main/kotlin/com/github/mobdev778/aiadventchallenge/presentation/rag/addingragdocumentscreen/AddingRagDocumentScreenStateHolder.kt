package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator
import com.github.mobdev778.aiadventchallenge.domain.rag.filechunker.FileChunkerFactory
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocument
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.model.AddingRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState.ChunkingStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.io.File
import java.util.UUID

/**
 * Держатель состояния экрана добавления документа в RAG-систему.
 *
 * Управляет жизненным циклом процесса загрузки, чанкинга и индексации документа.
 * Реализует поток команд ([AddingRagDocumentScreenCommand]) для навигации и
 * реагирует на пользовательские события ([AddingRagDocumentScreenEvent]).
 *
 * Аннотирован как [Factory], чтобы каждый экран получал новый экземпляр через Koin.
 *
 * @property ragDocumentRepository репозиторий для сохранения документов и чанков
 * @property ragConfigRepository репозиторий конфигурации RAG (зарезервирован для будущих доработок)
 * @property rankerFactory фабрика ранкеров, предоставляющая модель эмбеддингов для векторизации чанков
 * @property scope корутин-скоуп, в котором выполняются длительные операции (диспетчер [Dispatchers.IO])
 */
@Factory
class AddingRagDocumentScreenStateHolder(
    private val ragDocumentRepository: RagDocumentRepository,
    @Suppress("UnusedPrivateProperty")
    private val ragConfigRepository: RagConfigRepository,
    private val rankerFactory: RankerFactory,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddingRagDocumentScreenState())
    val state: StateFlow<AddingRagDocumentScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<AddingRagDocumentScreenCommand>(
        extraBufferCapacity = 1,
    )

    private var progressJob: Job? = null

    /**
     * Запускает фоновую операцию добавления нового RAG-документа.
     *
     * Создаёт запись документа, подсчитывает общее количество чанков для расчёта прогресса,
     * последовательно читает чанки с помощью [FileChunkerFactory], векторизует их через
     * [RagChunkGenerator] и сохраняет через [ragDocumentRepository]. Прогресс обновляется
     * в реальном времени в [state].
     * По завершении отправляет команду [AddingRagDocumentScreenCommand.BackToDocumentList].
     *
     * Если операция уже выполняется ([progressJob] не равен null), повторный вызов игнорируется.
     *
     * @param source путь к файлу-источнику
     * @param title заголовок документа
     * @param chunkingStrategy стратегия разбиения файла на чанки
     */
    @Suppress("MagicNumber")
    fun start(
        source: String,
        title: String,
        chunkingStrategy: ChunkingStrategy,
    ) {
        if (progressJob != null) return

        progressJob = scope.launch(Dispatchers.IO) {
            val document = RagDocument(
                id = UUID.randomUUID(),
                source = source,
                title = title,
            )
            ragDocumentRepository.createDocument(document)

            val total = getTotalChunks(source, chunkingStrategy)
            println("!!! total chunks: $total")

            val ragChunkGenerator = RagChunkGenerator(
                document.id,
                rankerFactory.embeddingModel
            )

            var processed = 0

            val fileChunker = FileChunkerFactory().create(File(source), chunkingStrategy)
            while (true) {
                val fileChunk = fileChunker.next() ?: break
                if (fileChunk.text.isNotEmpty()) {
                    val ragChunk = ragChunkGenerator.generate(fileChunk.section, fileChunk.text)
                    ragDocumentRepository.add(ragChunk)
                    processed++
                    if (processed % 25 == 0) {
                        println("!!! processed: $processed, total: $total")
                        _state.update { it.copy(progress = processed.toFloat() / total.toFloat()) }
                    }
                }
            }

            commands.tryEmit(AddingRagDocumentScreenCommand.BackToDocumentList)
        }
    }

    /**
     * Обрабатывает поступающие от UI события.
     *
     * @param event событие, произошедшее на экране добавления документа
     */
    fun onEvent(event: AddingRagDocumentScreenEvent) {
        when (event) {
            AddingRagDocumentScreenEvent.OnAbortClick -> abort()
        }
    }

    /**
     * Прерывает текущую операцию добавления документа, отменяя фоновую корутину.
     * Сбрасывает прогресс в исходное состояние и отправляет команду возврата
     * к форме добавления ([AddingRagDocumentScreenCommand.BackToAddDocument]).
     */
    private fun abort() {
        progressJob?.cancel()
        progressJob = null
        _state.update { it.copy(progress = 0f) }
        commands.tryEmit(AddingRagDocumentScreenCommand.BackToAddDocument)
    }

    /**
     * Вспомогательный метод для предварительного подсчёта количества чанков,
     * которые будут получены из файла при заданной стратегии разбиения.
     *
     * @param source путь к файлу-источнику
     * @param chunkingStrategy стратегия разбиения на чанки
     * @return общее число чанков, которое может быть извлечено из файла
     */
    private fun getTotalChunks(source: String, chunkingStrategy: ChunkingStrategy): Int {
        println("!!! AddingRagDocumentScreenStateHolder. getTotalChunks: $source")
        val fileChunker = FileChunkerFactory().create(File(source), chunkingStrategy)
        var chunks = 0
        while (fileChunker.next() != null) {
            chunks++
        }
        return chunks
    }
}
