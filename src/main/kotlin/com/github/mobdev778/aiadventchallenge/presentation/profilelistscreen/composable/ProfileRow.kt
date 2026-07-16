package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.RadioButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Composable-элемент, представляющий одну строку в списке профилей.
 *
 * Отображает радио-кнопку для выбора профиля, имя профиля (кликабельное для редактирования)
 * и кнопку удаления. Кнопка удаления недоступна для дефолтного профиля, который нельзя удалить.
 *
 * @param profile Модель данных профиля, содержащая его идентификатор, имя и статус выбранности.
 * @param onSelect Колбэк, вызываемый при выборе профиля (клик по всей строке или радио-кнопке).
 * @param onEdit Колбэк, вызываемый при клике на имя профиля для его редактирования.
 * @param onDelete Колбэк, вызываемый при нажатии на кнопку удаления профиля.
 */
@Composable
fun ProfileRow(
    profile: Profile,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val canDelete = profile.id != Profile.default.id

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            RadioButton(
                selected = profile.isSelected,
                onClick = onSelect,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth()
                    .clickable(onClick = onEdit),
                text = profile.name
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        DefaultButton(
            onClick = onDelete,
            enabled = canDelete,
        ) {
            Text("Удалить")
        }
    }
}
