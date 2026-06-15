package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.model

import java.util.UUID

data class EditProfileScreenState(
    val profileId: UUID? = null,
    val name: String = "",
    val content: String = "",
)
