package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState.ChunkingStrategy
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

/**
 * Хранитель состояния экрана добавления RAG-документа.
 *
 * Обеспечивает централизованное управление введёнными пользователем данными,
 * обработку событий от UI и выдачу команд для навигации или открытия диалогов.
 * Реализует паттерн Unidirectional Data Flow (UDF):
 * - состояние хранится в [MutableStateFlow] и доступно через [state];
 * - единичные действия (навигация) передаются через [commands] — [MutableSharedFlow].
 *
 * Класс зарегистрирован в Koin как Single.
 *
 * @property scope область корутин (в текущей реализации не используется).
 */
@Single
class AddRagDocumentScreenStateHolder(
    @Suppress("UnusedPrivateProperty")
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddRagDocumentScreenState())

    /**
     * Неизменяемое состояние экрана, предоставляемое в виде [StateFlow].
     */
    val state: StateFlow<AddRagDocumentScreenState> = _state.asStateFlow()

    /**
     * Поток команд (одноразовых действий), например, для навигации.
     * Имеет буфер на одно дополнительное значение для предотвращения блокировки отправителя.
     */
    val commands = MutableSharedFlow<AddRagDocumentScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Обрабатывает пользовательское событие, полученное от UI.
     * В зависимости от типа события обновляет состояние или испускает команду.
     *
     * @param event событие от экрана [AddRagDocumentScreenEvent].
     */
    fun onEvent(event: AddRagDocumentScreenEvent) {
        when (event) {
            AddRagDocumentScreenEvent.OnBackClick -> commands.tryEmit(AddRagDocumentScreenCommand.Back)
            AddRagDocumentScreenEvent.OnChooseSourceClick -> chooseSource()
            is AddRagDocumentScreenEvent.OnSourceChange -> _state.update { it.copy(source = event.value) }
            is AddRagDocumentScreenEvent.OnTitleChange -> _state.update { it.copy(title = event.value) }
            is AddRagDocumentScreenEvent.OnChunkingStrategyChange -> updateChunkingStrategy(event.index)
            AddRagDocumentScreenEvent.OnAddClick -> openAddingScreen()
        }
    }

    /**
     * Открывает стандартный диалог выбора файла и записывает путь выбранного файла
     * в состояние как источник документа.
     */
    private fun chooseSource() {
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        val chosenFile: VirtualFile? = FileChooser.chooseFile(descriptor, null, null)
        val path = chosenFile?.path ?: return
        _state.update { current -> current.copy(source = path) }
    }

    /**
     * Устанавливает стратегию разбиения текста на чанки по индексу в перечислении [ChunkingStrategy].
     * Если индекс некорректен, состояние не изменяется.
     *
     * @param index индекс выбранной стратегии.
     */
    private fun updateChunkingStrategy(index: Int) {
        val strategy = ChunkingStrategy.entries.getOrNull(index) ?: return
        _state.update { current -> current.copy(chunkingStrategy = strategy) }
    }

    /**
     * Проверяет заполнение полей «Источник» и «Название».
     * Если оба поля непустые (после удаления пробелов), испускает команду
     * [AddRagDocumentScreenCommand.OpenAddingDocument] с текущими значениями состояния.
     * В противном случае ничего не делает.
     */
    private fun openAddingScreen() {
        val snapshot = state.value
        val source = snapshot.source.trim()
        val title = snapshot.title.trim()
        if (source.isEmpty() || title.isEmpty()) return
        commands.tryEmit(
            AddRagDocumentScreenCommand.OpenAddingDocument(
                source = source,
                title = title,
                chunkingStrategy = snapshot.chunkingStrategy,
            )
        )
    }
}
