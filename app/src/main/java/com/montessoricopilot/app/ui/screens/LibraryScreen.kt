@file:OptIn(ExperimentalMaterial3Api::class)

package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.content.Areas
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.ui.ViewModelFactory
import com.montessoricopilot.app.ui.areaLabel
import com.montessoricopilot.app.ui.components.ActivityCard
import com.montessoricopilot.app.ui.components.StaggeredEntrance
import com.montessoricopilot.app.viewmodel.LibraryViewModel

/**
 * The whole curated library — browsable, searchable and filterable by
 * curriculum area, regardless of any one child's age. Age-filtered surfacing
 * for a specific child lives on Today.
 */
@Composable
fun LibraryScreen(
    contentRepository: ContentRepository,
    onActivityClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LibraryViewModel =
        viewModel(factory = ViewModelFactory { LibraryViewModel(contentRepository) })
    val state by viewModel.uiState.collectAsState()

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

        if (!state.isLoading && state.activities.isEmpty()) {
            Text(
                stringResource(R.string.no_activities_match),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(24.dp),
            )
            return@Column
        }

        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            itemsIndexed(state.activities, key = { _, a -> a.id }) { index, activity ->
                StaggeredEntrance(index) {
                    ActivityCard(
                        activity = activity,
                        onClick = { onActivityClick(activity.id) },
                    )
                }
            }
        }
    }
}
