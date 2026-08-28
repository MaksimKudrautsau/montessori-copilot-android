package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.viewmodel.ShelfItemUi
import com.montessoricopilot.app.viewmodel.ShelfViewModel

@Composable
fun ShelfScreen(
    childId: Int,
    shelfRepository: ShelfRepository,
    contentRepository: ContentRepository,
    modifier: Modifier = Modifier,
) {
    val viewModel: ShelfViewModel = viewModel(
        factory = ViewModelFactory { ShelfViewModel(childId, shelfRepository, contentRepository) },
    )
    val state by viewModel.uiState.collectAsState()
    var newItemTitle by remember { mutableStateOf("") }

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Row {
                OutlinedTextField(
                    value = newItemTitle,
                    onValueChange = { newItemTitle = it },
                    label = { Text("Add item to active shelf") },
                    modifier = Modifier.weight(1f),
                )
            }
            Button(
                onClick = { viewModel.addCustomItem(newItemTitle); newItemTitle = "" },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            ) { Text("Add") }
        }

        item { Text("Active shelf", style = MaterialTheme.typography.titleLarge) }
        items(state.active, key = { it.item.id }) { shelfItem ->
            ShelfRow(shelfItem, actionLabel = "Move to storage", onAction = { viewModel.moveToStorage(shelfItem.item) })
        }

        item {
            Text(
                "Storage rotation",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
        items(state.storage, key = { it.item.id }) { shelfItem ->
            ShelfRow(shelfItem, actionLabel = "Move to active shelf", onAction = { viewModel.moveToActiveShelf(shelfItem.item) })
        }
    }
}

@Composable
private fun ShelfRow(shelfItem: ShelfItemUi, actionLabel: String, onAction: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(shelfItem.title, style = MaterialTheme.typography.titleLarge)
            shelfItem.daysOnShelf?.let {
                Text("$it days on shelf", style = MaterialTheme.typography.bodyMedium)
            }
            if (shelfItem.dueForRotation) {
                Text(
                    "Due for rotation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            TextButton(onClick = onAction, modifier = Modifier.padding(top = 4.dp)) { Text(actionLabel) }
        }
    }
}
