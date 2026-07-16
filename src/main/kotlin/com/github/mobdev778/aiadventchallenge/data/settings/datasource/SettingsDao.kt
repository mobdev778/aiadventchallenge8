package com.github.mobdev778.aiadventchallenge.data.settings.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.settings.datasource.model.SettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO‑интерфейс для доступа к единственной строке настроек приложения в таблице `app_settings`.
 *
 * Все методы оперируют singleton‑записью с фиксированным первичным ключом [SettingsEntity.SINGLETON_ID].
 * Благодаря этому интерфейс гарантирует, что в источнике данных всегда присутствует ровно один экземпляр
 * конфигурации, а операции чтения и записи не требуют явного указания идентификатора при каждом вызове.
 */
@Dao
interface SettingsDao {

    /**
     * Возвращает холодный [Flow], который подписывается на изменения singleton‑записи настроек
     * и эмитит актуальные данные при каждом обновлении таблицы.
     *
     * @param id Идентификатор записи, по умолчанию равен [SettingsEntity.SINGLETON_ID].
     *           В подавляющем большинстве случаев переопределять его не требуется.
     * @return Поток ([Flow]), эмиттящий текущую сущность [SettingsEntity] либо `null`,
     *         если запись по каким‑либо причинам отсутствует (что нехарактерно для singleton‑паттерна).
     */
    @Query("SELECT * FROM app_settings WHERE id = :id LIMIT 1")
    fun observeById(id: Int = SettingsEntity.SINGLETON_ID): Flow<SettingsEntity?>

    /**
     * Приостанавливающая функция для однократного получения текущих настроек.
     *
     * @param id Идентификатор записи, по умолчанию равен [SettingsEntity.SINGLETON_ID].
     * @return Экземпляр [SettingsEntity] или `null`, если запись не найдена.
     */
    @Query("SELECT * FROM app_settings WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int = SettingsEntity.SINGLETON_ID): SettingsEntity?

    /**
     * Вставляет переданную сущность в таблицу, а при совпадении первичного ключа — полностью заменяет
     * существующую запись. Таким образом, реализуется атомарная операция обновления или создания
     * singleton‑записи настроек.
     *
     * @param entity Экземпляр [SettingsEntity], содержащий все актуальные поля конфигурации.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SettingsEntity)
}
