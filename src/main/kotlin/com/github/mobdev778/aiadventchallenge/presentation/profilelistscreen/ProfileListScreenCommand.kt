package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen

import java.util.UUID

sealed interface ProfileListScreenCommand {
    data object Back : ProfileListScreenCommand
    data object OpenAddProfile : ProfileListScreenCommand
    data class OpenEditProfile(val profileId: UUID) : ProfileListScreenCommand
}
