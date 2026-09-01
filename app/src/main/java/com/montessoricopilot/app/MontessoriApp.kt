package com.montessoricopilot.app

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.montessoricopilot.app.data.content.ContentDatabase
import com.montessoricopilot.app.data.user.UserDatabase
import com.montessoricopilot.app.work.WeeklyReminderWorker

/**
 * Application entry point.
 *
 * Starts the embedded Python interpreter once for the process lifetime, and
 * lazily builds both local databases:
 *  - [ContentDatabase]: the curated library, seeded synchronously on first
 *    creation from assets/content_seed.json, then read-only. Derived data —
 *    safe to drop and re-seed on a schema change.
 *  - [UserDatabase]: this household's own data (children, journal, shelf).
 *    Never destructively migrated.
 *
 * No application-scoped CoroutineScope is needed any more: content seeding is
 * synchronous inside Room's onCreate (see ContentSeed.kt), which removed the
 * first-launch race where a screen could read an empty library.
 */
class MontessoriApp : Application() {

    val contentDatabase: ContentDatabase by lazy { ContentDatabase.build(this) }
    val userDatabase: UserDatabase by lazy { UserDatabase.build(this) }

    override fun onCreate() {
        super.onCreate()
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        // Cheap and idempotent; creating the channel here means the system
        // notification settings show it before the first reminder ever fires.
        WeeklyReminderWorker.createChannel(this)
    }
}
