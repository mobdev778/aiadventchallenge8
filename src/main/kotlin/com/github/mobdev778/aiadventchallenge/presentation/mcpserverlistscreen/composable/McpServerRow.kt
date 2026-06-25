package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen.composable

import androidx.compose.foundation.clickable
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
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun McpServerRow(
    server: McpServer,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = server.active,
            onCheckedChange = onCheckedChange,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = server.name)
            Text(text = server.url)
        }

        Spacer(modifier = Modifier.width(8.dp))

        DefaultButton(onClick = onDeleteClick) {
            Text(text = "Удалить")
        }
    }
}
