@file:OptIn(ExperimentalMaterial3Api::class)

package com.montessoricopilot.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.content.LocalizedActivity
import com.montessoricopilot.app.ui.areaColor
import com.montessoricopilot.app.ui.areaLabel
import com.montessoricopilot.app.ui.messLevelLabel

/**
 * The standard activity row, used by both Library and Today.
 *
 * Image-led: the thumbnail anchors the row visually, which is what makes a long
 * list scannable. Metadata is one quiet line — area, age, duration, mess — since
 * those four are what a parent actually filters on in their head.
 */
@Composable
fun ActivityCard(
    activity: LocalizedActivity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** Optional line explaining why this is timely — already localised. */
    reasonPeriodName: String? = null,
    /** Optional trailing action, e.g. "Not now" on the Today screen. */
    trailing: @Composable (() -> Unit)? = null,
) {
    val area = stringResource(areaLabel(activity.area))
    val ageRange = stringResource(
        R.string.age_range_months, activity.ageMinMonths, activity.ageMaxMonths,
    )
    val session = stringResource(R.string.session_minutes, activity.sessionMinutes)
    val mess = stringResource(messLevelLabel(activity.messLevel))

    Card(modifier = modifier.fillMaxWidth().padding(vertical = 6.dp), onClick = onClick) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                ActivityThumbnail(area = activity.area, imageAsset = activity.imageAsset)

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        activity.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        area,
                        style = MaterialTheme.typography.labelLarge,
                        color = areaColor(activity.area),
                    )
                    Text(
                        "$ageRange · $session · $mess",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }
            }

            Text(
                activity.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp),
            )

            reasonPeriodName?.let { periodName ->
                Text(
                    stringResource(R.string.supports_sensitive_period, periodName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            trailing?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) { it() }
            }
        }
    }
}
