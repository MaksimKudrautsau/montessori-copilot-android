package com.montessoricopilot.app.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.prefs.ReminderPreference
import com.montessoricopilot.app.work.WeeklyReminderWorker

/**
 * Opt-in switch for the weekly reminder.
 *
 * Off by default, and it asks for notification permission only at the moment
 * the user turns it on — never at launch. Requesting a permission the user
 * hasn't asked for anything with is how apps get denied permanently.
 */
@Composable
fun WeeklyReminderToggle(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(ReminderPreference.isEnabled(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        // Only enable if the user actually granted it — otherwise the switch
        // would claim a reminder that can never appear.
        enabled = granted
        ReminderPreference.setEnabled(context, granted)
        if (granted) WeeklyReminderWorker.schedule(context)
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.weekly_reminder),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                stringResource(
                    if (enabled) R.string.weekly_reminder_on else R.string.weekly_reminder_off
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = { wantsOn ->
                if (!wantsOn) {
                    enabled = false
                    ReminderPreference.setEnabled(context, false)
                    WeeklyReminderWorker.cancel(context)
                    return@Switch
                }
                if (Build.VERSION.SDK_INT >= 33 &&
                    !WeeklyReminderWorker.canPostNotifications(context)
                ) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    enabled = true
                    ReminderPreference.setEnabled(context, true)
                    WeeklyReminderWorker.schedule(context)
                }
            },
        )
    }
}
