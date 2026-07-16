package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/**
 * Время ожидания (в миллисекундах) для стратегии [SharingStarted.WhileSubscribed],
 * определяющее, как долго удерживается активная подписка на [StateFlow] после
 * исчезновения всех коллекторов.
 */
private const val STATE_FLOW_TIMEOUT_MS = 5000L

/**
 * Холдер состояния экрана конфигурации Retrieval-Augmented Generation (RAG).
 *
 * Выступает посредником между UI-слоем и [RagConfigRepository], предоставляя
 * реактивное состояние текущей конфигурации ([uiState]) и канал команд
 * навигации ([commands]). Обрабатывает пользовательские события (например,
 * изменение параметров или выбор файла) через [onEvent], инициируя
 * соответствующие обновления в репозитории.
 *
 * Создаётся как синглтон (аннотация [Single]) и использует внешний
 * [CoroutineScope] для выполнения асинхронных операций.
 *
 * @property ragConfigRepository Репозиторий для доступа к конфигурации RAG.
 * @property scope Контекст корутины, в котором выполняются обновления.
 */
@Single
class RagConfigScreenStateHolder(
    private val ragConfigRepository: RagConfigRepository,
    private val scope: CoroutineScope,
) {

    /**
     * Реактивное состояние текущей конфигурации RAG. Поток значений эмиттится
     * при каждом изменении конфигурации в базе данных через [RagConfigRepository.observeConfig].
     * Использует стратегию [SharingStarted.WhileSubscribed] с таймаутом
     * [STATE_FLOW_TIMEOUT_MS] для экономии ресурсов.
     *
     * Начальным значением служит [RagConfigRepository.default].
     */
    val uiState: StateFlow<RagConfig> = ragConfigRepository
        .observeConfig()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS),
            initialValue = RagConfigRepository.default,
        )

    /**
     * Горячий поток ([MutableSharedFlow]) команд навигации, в который помещаются
     * одноразовые события, например, запрос на переход назад.
     * Имеет дополнительный буфер ёмкостью 1 для предотвращения потери команд
     * при быстром поступлении.
     */
    val commands = MutableSharedFlow<RagConfigScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Диспетчер событий экрана.
     *
     * В зависимости от типа поступившего [события][event] либо немедленно
     * отправляет команду (например, [RagConfigScreenEvent.OnBackClick]),
     * либо инициирует обновление конфигурации в репозитории. Для событий
     * выбора файла запускает диалог выбора файла с последующим асинхронным
     * обновлением соответствующего пути в конфигурации.
     *
     * @param event Пользовательское событие, требующее реакции.
     */
    fun onEvent(event: RagConfigScreenEvent) {
        when (event) {
            RagConfigScreenEvent.OnBackClick -> {
                commands.tryEmit(RagConfigScreenCommand.Back)
            }
            is RagConfigScreenEvent.OnTopKBeforeChanged -> updateConfig {
                copy(topKBefore = event.value)
            }
            is RagConfigScreenEvent.OnFilterTypeChanged -> updateConfig {
                copy(filterType = event.value)
            }
            is RagConfigScreenEvent.OnUseQueryRewritingChanged -> updateConfig {
                copy(useQueryRewriting = event.value)
            }
            is RagConfigScreenEvent.OnTopKAfterChanged -> updateConfig {
                copy(topKAfter = event.value)
            }
            is RagConfigScreenEvent.OnUseMinSimilarityChanged -> updateConfig {
                copy(useMinSimilarity = event.value)
            }
            is RagConfigScreenEvent.OnMinSimilarityChanged -> updateConfig {
                copy(minSimilarity = event.value)
            }
            is RagConfigScreenEvent.OnChooseRankerModelPathClick -> chooseFile(FileType.RankModel)
            is RagConfigScreenEvent.OnRankerModelPathChanged -> updateConfig {
                copy(rankerModelPath = event.value)
            }
            is RagConfigScreenEvent.OnChooseRankerTokenizerPathClick -> chooseFile(FileType.RankTokenizer)
            is RagConfigScreenEvent.OnRankerTokenizerPathChanged -> updateConfig {
                copy(rankerTokenizerPath = event.value)
            }
            is RagConfigScreenEvent.OnChooseEmbModelPathClick -> chooseFile(FileType.EmbModel)
            is RagConfigScreenEvent.OnEmbModelPathChanged -> updateConfig {
                copy(embModelPath = event.value)
            }
            is RagConfigScreenEvent.OnChooseEmbTokenizerPathClick -> chooseFile(FileType.EmbTokenizer)
            is RagConfigScreenEvent.OnEmbTokenizerPathChanged -> updateConfig {
                copy(embTokenizerPath = event.value)
            }
        }
    }

    /**
     * Открывает диалог выбора файла, соответствующий типу [action].
     *
     * Для моделей (реранкер, эмбеддинги) используются фильтры `*.onnx`,
     * для токенизаторов — `*.json`. После выбора файла асинхронно
     * (на [Dispatchers.IO]) считывает текущую конфигурацию из репозитория,
     * обновляет соответствующий путь и сохраняет изменения.
     *
     * @param action Тип файла, определяющий фильтр диалога и целевое поле конфигурации.
     */
    private fun chooseFile(action: FileType) {
        val descriptor = when (action) {
            FileType.RankModel -> FileChooserDescriptorFactory.createSingleFileDescriptor("onnx")
            FileType.RankTokenizer -> FileChooserDescriptorFactory.createSingleFileDescriptor("json")
            FileType.EmbModel -> FileChooserDescriptorFactory.createSingleFileDescriptor("onnx")
            FileType.EmbTokenizer -> FileChooserDescriptorFactory.createSingleFileDescriptor("json")
        }
        val chosenFile: VirtualFile? = FileChooser.chooseFile(descriptor, null, null)
        val path = chosenFile?.path ?: return
        scope.launch(Dispatchers.IO) {
            val oldConfig = ragConfigRepository.getConfig()
            val updatedConfig = when (action) {
                FileType.RankModel -> oldConfig.copy(rankerModelPath = path)
                FileType.RankTokenizer -> oldConfig.copy(rankerTokenizerPath = path)
                FileType.EmbModel -> oldConfig.copy(embModelPath = path)
                FileType.EmbTokenizer -> oldConfig.copy(embTokenizerPath = path)
            }
            ragConfigRepository.updateConfig(updatedConfig)
        }
    }

    /**
     * Применяет [трансформацию][transform] к текущей конфигурации RAG,
     * полученной из репозитория, и сохраняет обновлённый объект обратно.
     *
     * Выполняется асинхронно в контексте [Dispatchers.IO], чтобы не
     * блокировать поток UI.
     *
     * @param transform Лямбда-функция трансформации, возвращающая новую [RagConfig].
     */
    private fun updateConfig(transform: RagConfig.() -> RagConfig) {
        scope.launch(Dispatchers.IO) {
            val updatedConfig = ragConfigRepository.getConfig().transform()
            ragConfigRepository.updateConfig(updatedConfig)
        }
    }

    /**
     * Тип файла, выбираемого пользователем в диалоговом окне.
     * Каждый элемент соответствует определённому полю конфигурации
     * [RagConfig] и связан с характерным расширением.
     */
    enum class FileType {
        /** Модель реранкера (формат ONNX). */
        RankModel,
        /** Токенизатор модели реранкера (формат JSON). */
        RankTokenizer,
        /** Модель эмбеддингов (формат ONNX). */
        EmbModel,
        /** Токенизатор модели эмбеддингов (формат JSON). */
        EmbTokenizer,
    }
}
