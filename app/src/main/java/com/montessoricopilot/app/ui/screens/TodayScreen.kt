package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.montessoricopilot.app.data.repository.DailyRepository
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.ui.components.ActivityCard
import com.montessoricopilot.app.ui.components.MilestoneBanner
import com.montessoricopilot.app.ui.components.StaggeredEntrance
import com.montessoricopilot.app.viewmodel.TodayViewModel

@Composable
fun TodayScreen(
    childId: Int,
    childRepository: ChildRepository,
    contentRepository: ContentRepository,
    recommendationRepository: RecommendationRepository,
    shelfRepository: ShelfRepository,
    dailyRepository: DailyRepository,
    onActivityClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: TodayViewModel = viewModel(
        factory = ViewModelFactory {
            TodayViewModel(
                childId, childRepository, contentRepository,
                recommendationRepository, shelfRepository, dailyRepository,
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
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                    ),
                ) {
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

        state.milestone?.let { milestone ->
            item {
                MilestoneBanner(
                    childName = state.child?.name.orEmpty(),
                    milestone = milestone,
                )
            }
        }

        // Today's focus: one activity, given its own heading and full card, so
        // a parent with three minutes has a single obvious thing to do.
        state.focus?.let { focus ->
            item {
                Text(
                    stringResource(R.string.todays_focus),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
                ActivityCard(
                    activity = focus.activity,
                    onClick = { onActivityClick(focus.activity.id) },
                    reasonPeriodName = focus.reasonPeriodName,
                )
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
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(period.name, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        period.whatYoullNotice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    )
                }
            }
        }

        if (state.recommendations.isNotEmpty()) {
            item {
                Text(
                    stringResource(
                        if (state.focus != null) R.string.more_ideas
                        else R.string.suggested_activities
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
            }
        }
        itemsIndexed(
            state.recommendations,
            key = { _, rec -> rec.activity.id },
        ) { index, recommendation ->
            StaggeredEntrance(index) {
                ActivityCard(
                    activity = recommendation.activity,
                    onClick = { onActivityClick(recommendation.activity.id) },
                    reasonPeriodName = recommendation.reasonPeriodName,
                    trailing = {
                        TextButton(
                            onClick = {
                                viewModel.dismissRecommendation(recommendation.activity.id)
                            },
                        ) { Text(stringResource(R.string.action_not_now)) }
                    },
                )
            }
        }
    }
}
