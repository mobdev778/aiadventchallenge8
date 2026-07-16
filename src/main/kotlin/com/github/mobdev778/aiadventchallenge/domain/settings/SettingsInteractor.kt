package com.github.mobdev778.aiadventchallenge.domain.settings

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Интерактор для работы с настройками приложения.
 *
 * Предоставляет единый интерфейс для компонентов доменного слоя,
 * скрывая детали взаимодействия с [SettingsRepository]. Позволяет
 * реактивно отслеживать текущие [AppSettings] и атомарно их обновлять.
 * Кэш репозитория синхронно доступен через {@link SettingsRepository#getSettingsCache()},
 * а данный интерактор обеспечивает потоковую подписку на изменения и безопасную запись.
 *
 * @property repository репозиторий, отвечающий за хранение и предоставление настроек.
 */
@Single
class SettingsInteractor(
    val repository: SettingsRepository,
) {

    /**
     * Возвращает холодный поток [Flow] с последним значением [AppSettings],
     * автоматически обновляемый при каждом вызове [SettingsRepository.updateSettings]
     * или ином изменении источника данных репозитория.
     *
     * @return [Flow], эмитирующий текущие настройки при каждой модификации.
     */
    fun observeSettings(): Flow<AppSettings> {
        return repository.observeSettings()
    }

    /**
     * Обновляет настройки приложения, сохраняя переданный объект [AppSettings]
     * через репозиторий. Сохранение выполняется асинхронно с поддержкой
     * suspend и гарантирует, что все подписчики [observeSettings]
     * получат новое значение.
     *
     * @param settings новый набор настроек, который должен заменить предыдущий.
     */
    suspend fun update(settings: AppSettings) {
        repository.updateSettings(settings)
    }
}
