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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.Recommendation
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.ui.areaLabel
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
            TodayViewModel(
                childId, childRepository, contentRepository,
                recommendationRepository, shelfRepository,
            )
        },
    )
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) return

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Text(
                stringResource(
                    R.string.today_header,
                    state.child?.name.orEmpty(),
                    state.ageMonths,
                ),
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        if (state.itemsDueForRotation > 0) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(
                        pluralStringResource(
                            R.plurals.items_due_for_rotation,
                            state.itemsDueForRotation,
                            state.itemsDueForRotation,
                        ),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        if (state.activePeriods.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.active_sensitive_periods),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
            }
            items(state.activePeriods, key = { it.id }) { period ->
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(period.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        period.whatYoullNotice,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        item {
            Text(
                stringResource(R.string.suggested_activities),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
        }
        items(state.recommendations, key = { it.activity.id }) { recommendation ->
            RecommendationCard(
                recommendation = recommendation,
                onDismiss = { viewModel.dismissRecommendation(recommendation.activity.id) },
            )
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: Recommendation, onDismiss: () -> Unit) {
    val activity = recommendation.activity
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(activity.title, style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(areaLabel(activity.area)),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                activity.summary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            // The period name arrives localised; the sentence around it comes
            // from a string resource, so this line is fully translated.
            recommendation.reasonPeriodName?.let { periodName ->
                Text(
                    stringResource(R.string.supports_sensitive_period, periodName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 4.dp)) {
                Text(stringResource(R.string.action_not_now))
            }
        }
    }
}
