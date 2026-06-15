package com.github.mobdev778.aiadventchallenge.data.profile.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.mobdev778.aiadventchallenge.data.profile.datasource.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles")
    suspend fun getAll(): List<ProfileEntity>

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<ProfileEntity>)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE profiles SET is_selected = 0")
    suspend fun clearSelection()

    @Query("UPDATE profiles SET is_selected = 1 WHERE id = :id")
    suspend fun setSelected(id: String)

    @Transaction
    suspend fun selectOnly(id: String) {
        clearSelection()
        setSelected(id)
    }
}
