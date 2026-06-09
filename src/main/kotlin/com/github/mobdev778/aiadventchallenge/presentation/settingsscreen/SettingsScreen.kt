package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import kotlin.random.Random

@Composable
fun SettingsScreen() {
    val labelText = remember { mutableStateOf("The random number is: ?") }

    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(labelText.value)

        OutlinedButton(onClick = {
            labelText.value = "The random number is: " + Random(System.currentTimeMillis()).nextInt(1000)
        }) { Text("Shuffle") }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}