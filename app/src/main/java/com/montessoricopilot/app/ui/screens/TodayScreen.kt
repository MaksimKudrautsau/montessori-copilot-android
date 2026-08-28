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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.logic.RecommendedActivityResult
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.viewmodel.TodayViewModel

@Composable
fun TodayScreen(
    childId: Int,
    childRepository: ChildRepository,
    contentRepository: ContentRepository,
    recommendationRepository: RecommendationRepository,
    shelfRepository: ShelfRepository,
    modifier: Modifier = Modifier,
) {
    val viewModel: TodayViewModel = viewModel(
        factory = ViewModelFactory {
            TodayViewModel(childId, childRepository, contentRepository, recommendationRepository, shelfRepository)
        },
    )
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) return

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Text(
                "${state.child?.name} · ${state.ageMonths} months",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        if (state.itemsDueForRotation > 0) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(
                        "${state.itemsDueForRotation} shelf item(s) may be ready to rotate — see the Shelf tab.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        if (state.activePeriods.isNotEmpty()) {
            item {
                Text(
                    "Active sensitive periods",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
            }
            items(state.activePeriods) { period ->
                Text("• ${period.periodName}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            Text(
                "Suggested activities",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
        }
        items(state.recommendations, key = { it.id }) { recommendation ->
            RecommendationCard(recommendation, onDismiss = { viewModel.dismissRecommendation(recommendation.id) })
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: RecommendedActivityResult, onDismiss: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(recommendation.title, style = MaterialTheme.typography.titleLarge)
            Text(recommendation.category.replace('_', ' '), style = MaterialTheme.typography.bodyMedium)
            recommendation.reason?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }
            TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 4.dp)) {
                Text("Not now")
            }
        }
    }
}
