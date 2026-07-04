package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen.ProfileListScreenEvent
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ProfileListScreenContent(
    profiles: List<Profile>,
    onEvent: (ProfileListScreenEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(ProfileListScreenEvent.OnBackClick) }) {
                Text("Назад")
            }

            DefaultButton(onClick = { onEvent(ProfileListScreenEvent.OnAddProfileClick) }) {
                Text("Добавить")
            }
        }

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Профили",
        )

        if (profiles.isEmpty()) {
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = "Профилей пока нет. Нажмите 'Добавить'.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = profiles,
                    key = { it.id },
                ) { profile ->
                    ProfileRow(
                        profile = profile,
                        onSelect = {
                            onEvent(ProfileListScreenEvent.OnSelectProfile(profile.id))
                        },
                        onEdit = {
                            onEvent(ProfileListScreenEvent.OnEditProfile(profile.id))
                        },
                        onDelete = {
                            onEvent(ProfileListScreenEvent.OnDeleteProfileClick(profile.id))
                        },
                    )
                }
            }
        }
    }
}
