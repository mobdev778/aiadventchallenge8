package com.github.mobdev778.aiadventchallenge.data.rag.repository

import com.github.mobdev778.aiadventchallenge.data.rag.datasource.RagDocumentDao
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagConfigEntity
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Репозиторий, предоставляющий единую точку доступа к конфигурации
 * Retrieval-Augmented Generation (RAG). Отвечает за преобразование
 * сущностей слоя данных ([RagConfigEntity]) в доменную модель
 * ([RagConfig]) и обратно, а также за кэширование настроек по умолчанию.
 *
 * Реализован как синглтон с помощью аннотации [Single] (Koin) и использует
 * [RagDocumentDao] для взаимодействия с базой данных. Позволяет как
 * реактивно наблюдать изменения, так и получать / обновлять конфигурацию
 * в приостанавливающем стиле.
 */
@Single
class RagConfigRepository(
    private val ragDocumentDao: RagDocumentDao,
) {

    /**
     * Возвращает холодный поток ([Flow]) текущей конфигурации RAG,
     * который автоматически эмиттит новые значения при изменении
     * записи в базе данных. Если сохранённая конфигурация отсутствует,
     * используется значение по умолчанию [default].
     *
     * Оператор [distinctUntilChanged] гарантирует, что одинаковые
     * подряд идущие значения не будут переизлучены.
     *
     * @return [Flow] с текущим объектом [RagConfig].
     */
    fun observeConfig(): Flow<RagConfig> =
        ragDocumentDao.observeConfig()
            .map { entity -> entity?.toDomain() ?: default }
            .distinctUntilChanged()

    /**
     * Приостанавливающая функция для однократного получения актуальной
     * конфигурации RAG. Если сохранённая конфигурация отсутствует,
     * возвращает значение по умолчанию [default].
     *
     * @return Текущая конфигурация [RagConfig].
     */
    suspend fun getConfig(): RagConfig {
        return ragDocumentDao.getConfig()?.toDomain() ?: default
    }

    /**
     * Сохраняет новую конфигурацию RAG в базе данных. Доменная модель
     * преобразуется в сущность [RagConfigEntity] и вставляется
     * с флагом [OnConflictStrategy.REPLACE] через [RagDocumentDao.upsertConfig].
     *
     * @param config Новая конфигурация, которую необходимо сохранить.
     */
    suspend fun updateConfig(config: RagConfig) {
        ragDocumentDao.upsertConfig(config.toEntity())
    }

    /**
     * Преобразует сущность [RagConfigEntity] в доменную модель [RagConfig].
     * Тип фильтра восстанавливается из строкового представления с откатом
     * на [RagFilterType.Similarity] в случае невалидного значения.
     */
    private fun RagConfigEntity.toDomain(): RagConfig {
        val filterType = runCatching { RagFilterType.valueOf(filterType) }
            .getOrDefault(RagFilterType.Similarity)

        return RagConfig(
            topKBefore = topKBefore,
            filterType = filterType,
            useQueryRewriting = useQueryRewriting,
            topKAfter = topKAfter,
            useMinSimilarity = useMinSimilarity,
            minSimilarity = minSimilarity,
            rankerModelPath = rankerModelPath,
            rankerTokenizerPath = rankerTokenizerPath,
            embModelPath = embModelPath,
            embTokenizerPath = embTokenizerPath,
        )
    }

    /**
     * Преобразует доменную модель в сущность базы данных.
     * Использует синглтонный идентификатор [RagConfigEntity.SINGLETON_ID].
     */
    private fun RagConfig.toEntity(): RagConfigEntity =
        RagConfigEntity(
            id = RagConfigEntity.SINGLETON_ID,
            topKBefore = topKBefore,
            filterType = filterType.name,
            useQueryRewriting = useQueryRewriting,
            topKAfter = topKAfter,
            useMinSimilarity = useMinSimilarity,
            minSimilarity = minSimilarity,
            rankerModelPath = rankerModelPath,
            rankerTokenizerPath = rankerTokenizerPath,
            embModelPath = embModelPath,
            embTokenizerPath = embTokenizerPath,
        )

    companion object {
        /**
         * Значение конфигурации RAG по умолчанию, используемое,
         * если реальная запись в базе данных отсутствует.
         */
        val default = RagConfig(
            topKBefore = 10,
            filterType = RagFilterType.Similarity,
            useQueryRewriting = false,
            topKAfter = 5,
            useMinSimilarity = false,
            minSimilarity = 0.5,
            rankerModelPath = "",
            rankerTokenizerPath = "",
            embModelPath = "",
            embTokenizerPath = "",
        )
    }
}
