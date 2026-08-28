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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.data.repository.JournalRepository
import com.montessoricopilot.app.data.user.JournalEntryEntity
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.viewmodel.JournalViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, HH:mm")

@Composable
fun JournalScreen(childId: Int, journalRepository: JournalRepository, modifier: Modifier = Modifier) {
    val viewModel: JournalViewModel = viewModel(factory = ViewModelFactory { JournalViewModel(childId, journalRepository) })
    val entries by viewModel.entries.collectAsState()
    var note by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("What did you observe?") },
                modifier = Modifier.weight(1f),
            )
        }
        Button(
            onClick = { viewModel.addEntry(category = "other", note = note); note = "" },
            modifier = Modifier.padding(top = 8.dp),
        ) { Text("Log entry") }

        LazyColumn(contentPadding = PaddingValues(top = 16.dp)) {
            items(entries, key = { it.id }) { entry -> JournalRow(entry) }
        }
    }
}

@Composable
private fun JournalRow(entry: JournalEntryEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            val timestamp = Instant.ofEpochMilli(entry.timestampEpochMillis).atZone(ZoneId.systemDefault())
            Text(DATE_FORMAT.format(timestamp), style = MaterialTheme.typography.bodyMedium)
            Text(entry.note, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
