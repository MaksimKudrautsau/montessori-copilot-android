@file:OptIn(ExperimentalMaterial3Api::class)

package com.montessoricopilot.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.ui.components.WeeklyReminderToggle

private data class ImageAttribution(
    val title: String,
    val credit: String,
    val licence: String,
)

/**
 * Content sources and image credits.
 *
 * This screen is a licensing requirement, not a nicety: Wikimedia Commons
 * images are free to use *with attribution*, and each file carries its own
 * licence (PRD v0.5 §6.4). Building it before any images exist means credits
 * can never be forgotten when they land — an image without a recorded credit
 * simply won't appear here, which is a visible gap rather than a silent breach.
 */
@Composable
fun AttributionsScreen(contentRepository: ContentRepository, onBack: () -> Unit) {
    var attributions by remember { mutableStateOf<List<ImageAttribution>>(emptyList()) }

    LaunchedEffect(Unit) {
        attributions = contentRepository.allActivities()
            .filter { it.imageCredit != null }
            .map {
                ImageAttribution(
                    title = it.title,
                    credit = it.imageCredit.orEmpty(),
                    licence = it.imageLicence.orEmpty(),
                )
            }
            .sortedBy { it.title }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.attributions_title)) },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                // Settings live here rather than in their own screen: with a
                // single toggle, a dedicated Settings destination would be an
                // empty room.
                WeeklyReminderToggle()
                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                Text(
                    stringResource(R.string.attributions_content_heading),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    stringResource(R.string.attributions_content_body),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Text(
                    stringResource(R.string.disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))
                Text(
                    stringResource(R.string.attributions_images_heading),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            if (attributions.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.attributions_no_images),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            } else {
                items(attributions) { attribution ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(attribution.title, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${attribution.credit} · ${attribution.licence}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        }
    }
}
