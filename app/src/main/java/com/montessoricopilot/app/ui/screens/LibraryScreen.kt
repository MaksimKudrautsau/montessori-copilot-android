@file:OptIn(ExperimentalMaterial3Api::class)

package com.montessoricopilot.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.content.Areas
import com.montessoricopilot.app.data.content.LocalizedActivity
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.ui.areaLabel
import com.montessoricopilot.app.ui.messLevelLabel
import com.montessoricopilot.app.viewmodel.LibraryViewModel

/**
 * The whole curated library — browsable and searchable regardless of any one
 * child's age. Age-filtered surfacing for a specific child lives on Today.
 *
 * Cards expand in place rather than navigating to a detail screen: P0 only
 * needs the new content fields to be visible and verifiable. The proper
 * image-led detail screen is E2.
 */
@Composable
fun LibraryScreen(contentRepository: ContentRepository, modifier: Modifier = Modifier) {
    val viewModel: LibraryViewModel =
        viewModel(factory = ViewModelFactory { LibraryViewModel(contentRepository) })
    val state by viewModel.uiState.collectAsState()

    // -1 means nothing expanded. Only one card is open at a time, which keeps
    // the list scannable.
    var expandedId by remember { mutableIntStateOf(-1) }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChanged,
            label = { Text(stringResource(R.string.search_activities)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // One chip per curriculum area — also how the four areas added in P0
        // become visible to the user at all.
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Areas.ALL.forEach { area ->
                val selected = state.selectedArea == area
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.onAreaSelected(if (selected) null else area) },
                    label = { Text(stringResource(areaLabel(area))) },
                )
            }
        }

        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(state.activities, key = { it.id }) { activity ->
                ActivityCard(
                    activity = activity,
                    expanded = expandedId == activity.id,
                    onToggle = { expandedId = if (expandedId == activity.id) -1 else activity.id },
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(
    activity: LocalizedActivity,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    // Each piece resolved separately: stringResource is @Composable and cannot
    // be called from inside a plain string builder lambda.
    val area = stringResource(areaLabel(activity.area))
    val ageRange = stringResource(
        R.string.age_range_months, activity.ageMinMonths, activity.ageMaxMonths,
    )
    val session = stringResource(R.string.session_minutes, activity.sessionMinutes)
    val mess = stringResource(messLevelLabel(activity.messLevel))

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), onClick = onToggle) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(activity.title, style = MaterialTheme.typography.titleLarge)
            Text(
                "$area · $ageRange · $session · $mess",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                activity.summary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Section(R.string.section_why_it_matters, activity.whyItMatters)
                    Section(R.string.section_how_to_present, activity.howToPresent)
                    Section(R.string.section_what_to_observe, activity.whatToObserve)
                    Section(R.string.section_common_mistakes, activity.commonMistakes)
                    Section(R.string.section_materials, activity.materialsNeeded)
                    Section(R.string.section_homemade, activity.homemadeAlternative)
                    activity.supervisionNote?.let {
                        Section(R.string.section_supervision, it)
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(titleRes: Int, body: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            stringResource(titleRes),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}
