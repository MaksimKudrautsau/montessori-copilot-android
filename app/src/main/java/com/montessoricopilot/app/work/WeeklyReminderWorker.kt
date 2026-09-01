package com.montessoricopilot.app.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.montessoricopilot.app.MontessoriApp
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.DailyRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * One quiet reminder a week.
 *
 * Weekly, not daily, and deliberately so: PRD v0.5 principle 3 is that the app
 * must not manufacture compulsion, because an app that nags contradicts the
 * pedagogy it teaches. It also only fires when there is something concrete to
 * say — a shelf item genuinely stale, or a milestone genuinely approaching.
 * Silence is the correct output most weeks.
 */
class WeeklyReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? MontessoriApp ?: return Result.success()

        // Nothing to say if notifications aren't permitted — quietly succeed
        // rather than retrying forever.
        if (!canPostNotifications(applicationContext)) return Result.success()

        val userDao = app.userDatabase.userDao()
        val contentDao = app.contentDatabase.contentDao()
        val contentRepository = ContentRepository(contentDao)
        val shelfRepository = ShelfRepository(userDao)
        val dailyRepository = DailyRepository(contentDao, userDao, contentRepository)

        val children = userDao.observeChildren().first()
        if (children.isEmpty()) return Result.success()

        for (child in children) {
            val shelfItems = shelfRepository.observeForChild(child.id).first()
            val dueCount = shelfRepository.rotationStatus(shelfItems).count { it.dueForRotation }
            if (dueCount > 0) {
                notify(
                    id = child.id * 10,
                    title = applicationContext.getString(R.string.notification_rotation_title),
                    body = applicationContext.resources.getQuantityString(
                        R.plurals.items_due_for_rotation, dueCount, dueCount,
                    ),
                )
                continue  // one notification per child at most
            }

            val milestone = dailyRepository.upcomingMilestone(child.birthDateEpochDay)
            if (milestone != null) {
                notify(
                    id = child.id * 10 + 1,
                    title = applicationContext.getString(R.string.notification_milestone_title),
                    body = applicationContext.getString(
                        R.string.milestone_in_days,
                        child.name,
                        milestone.nextAgeMonths,
                        milestone.daysAway.toInt(),
                    ),
                )
            }
        }
        return Result.success()
    }

    private fun notify(id: Int, title: String, body: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(applicationContext).notify(id, notification)
        } catch (_: SecurityException) {
            // Permission revoked between the check and here; nothing to do.
        }
    }

    companion object {
        private const val CHANNEL_ID = "weekly_reminder"
        private const val WORK_NAME = "weekly_reminder"

        fun canPostNotifications(context: Context): Boolean =
            android.os.Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED

        fun createChannel(context: Context) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                // LOW: appears in the shade without a sound or heads-up
                // interruption. A parenting reminder should never buzz.
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }
            manager.createNotificationChannel(channel)
        }

        /** Idempotent — KEEP means an existing schedule is never restarted, so
         *  the reminder doesn't drift earlier every time the app launches. */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WeeklyReminderWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(7, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request,
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
