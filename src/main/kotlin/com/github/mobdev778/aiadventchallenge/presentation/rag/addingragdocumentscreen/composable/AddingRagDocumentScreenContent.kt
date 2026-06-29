package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.AddingRagDocumentScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.model.AddingRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AddingRagDocumentScreenContent(
    state: AddingRagDocumentScreenState,
    onEvent: (AddingRagDocumentScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Добавление RAG документа",
        )

        Spacer(modifier = Modifier.size(24.dp))

        RagDocumentProgressBar(
            modifier = Modifier.fillMaxWidth(),
            progress = state.progress,
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = "${(state.progress * 100).toInt()}%")

        Spacer(modifier = Modifier.size(16.dp))

        DefaultButton(onClick = { onEvent(AddingRagDocumentScreenEvent.OnAbortClick) }) {
            Text("Прервать")
        }
    }
}

@Composable
private fun RagDocumentProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(999.dp)

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.White,
                shape = shape,
            )
            .background(
                color = Color(0xFF2B2B2B),
                shape = shape,
            )
            .padding(2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(10.dp)
                .background(
                    color = Color(0xFF39FF14),
                    shape = shape,
                ),
        )
    }
}
