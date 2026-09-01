@file:OptIn(ExperimentalMaterial3Api::class)

package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.content.LocalizedActivity
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.ui.areaColor
import com.montessoricopilot.app.ui.areaLabel
import com.montessoricopilot.app.ui.components.ActivityImage
import com.montessoricopilot.app.ui.messLevelLabel
import com.montessoricopilot.app.ui.provenanceLabel

/**
 * The full activity: what it is, why it matters, how to present it, what to
 * watch for, and what to avoid.
 *
 * The "how to present it" section is the reason this screen exists. Montessori
 * presentation is a specific technique — slow, silent, hands modelled before
 * words — and it is the single thing an untrained parent is most likely to be
 * missing (PRD v0.5 §5 E1).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivityDetailScreen(
    activityId: Int,
    contentRepository: ContentRepository,
    onBack: () -> Unit,
) {
    var activity by remember(activityId) { mutableStateOf<LocalizedActivity?>(null) }

    LaunchedEffect(activityId) {
        activity = contentRepository.activity(activityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        activity?.title.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val current = activity ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            ActivityImage(
                area = current.area,
                imageAsset = current.imageAsset,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                cornerRadius = 0.dp,
                iconSize = 64.dp,
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(areaLabel(current.area)),
                    style = MaterialTheme.typography.labelLarge,
                    color = areaColor(current.area),
                )
                Text(current.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    current.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                stringResource(
                                    R.string.age_range_months,
                                    current.ageMinMonths,
                                    current.ageMaxMonths,
                                )
                            )
                        },
                    )
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(stringResource(R.string.session_minutes, current.sessionMinutes))
                        },
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(messLevelLabel(current.messLevel))) },
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Section(R.string.section_why_it_matters, current.whyItMatters)
                Section(R.string.section_how_to_present, current.howToPresent, emphasised = true)
                Section(R.string.section_what_to_observe, current.whatToObserve)
                Section(R.string.section_common_mistakes, current.commonMistakes)
                Section(R.string.section_materials, current.materialsNeeded)
                Section(R.string.section_homemade, current.homemadeAlternative)

                current.supervisionNote?.let { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                        ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                stringResource(R.string.section_supervision),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(note, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Provenance is shown, not hidden — the app makes claims about
                // a pedagogy, so where each claim came from is the user's
                // business (PRD v0.5 §6).
                Text(
                    stringResource(R.string.source_label,
                        stringResource(provenanceLabel(current.provenance))),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
                current.imageCredit?.let { credit ->
                    Text(
                        stringResource(R.string.image_credit_label, credit),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(titleRes: Int, body: String, emphasised: Boolean = false) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            stringResource(titleRes),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            body,
            style = if (emphasised) {
                MaterialTheme.typography.bodyLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
