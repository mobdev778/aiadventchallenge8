package com.github.mobdev778.aiadventchallenge.data.profile.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.mobdev778.aiadventchallenge.data.profile.datasource.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Объект доступа к данным (DAO) для работы с таблицей профилей [ProfileEntity].
 *
 * Предоставляет методы для выполнения операций чтения, записи, обновления и удаления профилей,
 * а также для управления выбором активного профиля. Все операции выполняются асинхронно,
 * а наблюдение за списком профилей реализовано через [Flow].
 */
@Dao
interface ProfileDao {

    /**
     * Наблюдает за всеми профилями, возвращая реактивный поток списка,
     * отсортированного по имени без учёта регистра в порядке возрастания (A-Z).
     *
     * @return [Flow], эмиттирующий актуальный список [ProfileEntity] при каждом изменении данных.
     */
    @Query("SELECT * FROM profiles ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ProfileEntity>>

    /**
     * Возвращает полный список всех профилей в виде единовременного запроса.
     *
     * @return Список всех записей [ProfileEntity] из таблицы.
     */
    @Query("SELECT * FROM profiles")
    suspend fun getAll(): List<ProfileEntity>

    /**
     * Ищет профиль по его уникальному идентификатору.
     *
     * @param id Идентификатор профиля.
     * @return Найденный профиль [ProfileEntity] или `null`, если профиль с таким id отсутствует.
     */
    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProfileEntity?

    /**
     * Вставляет профиль или заменяет существующий с таким же первичным ключом.
     * При конфликте используется стратегия [OnConflictStrategy.REPLACE].
     *
     * @param entity Экземпляр [ProfileEntity] для вставки или замены.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProfileEntity)

    /**
     * Вставляет или заменяет сразу несколько профилей за одну транзакцию.
     * При конфликте по первичному ключу запись заменяется.
     *
     * @param entities Список профилей [ProfileEntity] для вставки или замены.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<ProfileEntity>)

    /**
     * Удаляет профиль по его идентификатору.
     *
     * @param id Идентификатор удаляемого профиля.
     */
    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Сбрасывает флаг выбора (`isSelected`) для всех профилей, делая ни один профиль не выбранным.
     */
    @Query("UPDATE profiles SET is_selected = 0")
    suspend fun clearSelection()

    /**
     * Устанавливает флаг выбора (`isSelected`) в `true` для профиля с указанным идентификатором.
     *
     * @param id Идентификатор профиля, который должен стать выбранным.
     */
    @Query("UPDATE profiles SET is_selected = 1 WHERE id = :id")
    suspend fun setSelected(id: String)

    /**
     * Транзакционная операция, которая сбрасывает выбор со всех профилей,
     * а затем помечает как выбранный только указанный профиль.
     * Гарантирует, что в любой момент времени может быть выбран не более одного профиля.
     *
     * @param id Идентификатор профиля, который необходимо сделать единственным выбранным.
     */
    @Transaction
    suspend fun selectOnly(id: String) {
        clearSelection()
        setSelected(id)
    }
}
