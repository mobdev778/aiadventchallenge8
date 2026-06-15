package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen

import java.util.UUID

sealed interface ProfileListScreenEvent {
    data object OnBackClick : ProfileListScreenEvent
    data object OnAddProfileClick : ProfileListScreenEvent
    data class OnDeleteProfileClick(val profileId: UUID) : ProfileListScreenEvent
    data class OnSelectProfile(val profileId: UUID) : ProfileListScreenEvent
    data class OnEditProfile(val profileId: UUID) : ProfileListScreenEvent
}
