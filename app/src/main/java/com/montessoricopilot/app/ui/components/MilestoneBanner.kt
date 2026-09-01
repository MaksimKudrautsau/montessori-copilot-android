package com.montessoricopilot.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.repository.UpcomingMilestone

/**
 * "Emily turns 14 months this week — here's what's changing."
 *
 * Only rendered when a milestone is both near and meaningful; the repository
 * returns null for the quiet months, so this never shows an empty "nothing is
 * changing" card. That restraint is the point — a banner that appears every
 * day stops being read.
 */
@Composable
fun MilestoneBanner(childName: String, milestone: UpcomingMilestone) {
    val headline = when (milestone.daysAway) {
        0L -> stringResource(R.string.milestone_today, childName, milestone.nextAgeMonths)
        1L -> stringResource(R.string.milestone_tomorrow, childName, milestone.nextAgeMonths)
        else -> stringResource(
            R.string.milestone_in_days,
            childName,
            milestone.nextAgeMonths,
            milestone.daysAway.toInt(),
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(headline, style = MaterialTheme.typography.titleLarge)

            if (milestone.periodsStartingLocalized.isNotEmpty()) {
                Text(
                    stringResource(
                        R.string.milestone_periods_starting,
                        milestone.periodsStartingLocalized.joinToString(", "),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (milestone.periodsEndingLocalized.isNotEmpty()) {
                Text(
                    stringResource(
                        R.string.milestone_periods_ending,
                        milestone.periodsEndingLocalized.joinToString(", "),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (milestone.newlyEligible.isNotEmpty()) {
                Text(
                    stringResource(R.string.milestone_new_activities),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 12.dp),
                )
                milestone.newlyEligible.take(3).forEach { activity ->
                    Text(
                        "• ${activity.title}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
