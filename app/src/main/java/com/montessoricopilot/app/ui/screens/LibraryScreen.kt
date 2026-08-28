package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.data.content.ActivityEntity
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.viewmodel.LibraryViewModel

/** The whole curated content library — no AI, browsable and searchable
 *  regardless of any one child's current age. Age-filtered surfacing for a
 *  specific child happens on the Today tab instead. */
@Composable
fun LibraryScreen(contentRepository: ContentRepository, modifier: Modifier = Modifier) {
    val viewModel: LibraryViewModel = viewModel(factory = ViewModelFactory { LibraryViewModel(contentRepository) })
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChanged,
            label = { Text("Search activities") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
            items(state.activities, key = { it.id }) { activity -> ActivityCard(activity) }
        }
    }
}

@Composable
private fun ActivityCard(activity: ActivityEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(activity.title, style = MaterialTheme.typography.titleLarge)
            Text(
                "${activity.category.replace('_', ' ')} · ${activity.ageMinMonths}-${activity.ageMaxMonths} months",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(activity.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            Text(
                "Materials: ${activity.materialsNeeded}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
