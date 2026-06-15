package com.github.mobdev778.aiadventchallenge.data.profile.repository

import com.github.mobdev778.aiadventchallenge.data.profile.datasource.ProfileDao
import com.github.mobdev778.aiadventchallenge.data.profile.datasource.model.ProfileEntity
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class ProfileRepository(
    private val profileDao: ProfileDao,
) {

    fun observeProfiles(): Flow<List<Profile>> =
        profileDao.observeAll()
            .map { entities ->
                val profiles = entities.map { it.toDomain() }

                if (profiles.firstOrNull { it.id == Profile.default } == null) {
                    val defaultProfile = Profile.default.copy(
                        isSelected = profiles.isEmpty() || !profiles.any { it.isSelected }
                    )

                    listOf(defaultProfile) + profiles
                } else profiles
            }
            .distinctUntilChanged()

    /**
     * Returns first selected profile.
     * If there is no selected profile (or there are no profiles at all) -> returns [Profile.default].
     */
    suspend fun getSelectedProfile(): Profile {
        val selected = profileDao.getAll().firstOrNull { it.isSelected }?.toDomain()
        return selected ?: Profile.default
    }

    suspend fun createProfile(profile: Profile) {
        profileDao.upsert(profile.toEntity())
        ensureSingleSelectionAfterMutation()
    }

    suspend fun updateProfile(profile: Profile) {
        profileDao.upsert(profile.toEntity())
        ensureSingleSelectionAfterMutation()
    }

    suspend fun getProfile(profileId: UUID): Profile? =
        profileDao.getById(profileId.toString())?.toDomain()

    suspend fun deleteProfile(profileId: UUID) {
        val entity = profileDao.getById(profileId.toString())
        profileDao.deleteById(profileId.toString())

        // If deleted selected profile -> select first remaining.
        if (entity?.isSelected == true) {
            selectFirstIfAny()
        } else {
            ensureSingleSelectionAfterMutation()
        }
    }

    suspend fun selectProfile(profileId: UUID) {
        profileDao.selectOnly(profileId.toString())
    }

    /**
     * Guarantees invariant: at most one selected; if none selected and list not empty -> select first.
     */
    private suspend fun ensureSingleSelectionAfterMutation() {
        val all = profileDao.getAll()
        if (all.isEmpty()) return

        val selected = all.filter { it.isSelected }
        when {
            selected.isEmpty() -> {
                profileDao.selectOnly(all.first().id)
            }

            selected.size == 1 -> Unit

            else -> {
                // Keep first selected, clear others.
                val keepId = selected.first().id
                val fixed = all.map { it.copy(isSelected = it.id == keepId) }
                profileDao.upsertAll(fixed)
            }
        }
    }

    private suspend fun selectFirstIfAny() {
        val all = profileDao.getAll()
        if (all.isEmpty()) return
        profileDao.selectOnly(all.first().id)
    }

    private fun ProfileEntity.toDomain(): Profile =
        Profile(
            id = UUID.fromString(id),
            name = name,
            content = content,
            isSelected = isSelected,
        )

    private fun Profile.toEntity(): ProfileEntity =
        ProfileEntity(
            id = id.toString(),
            name = name,
            content = content,
            isSelected = isSelected,
        )
}
