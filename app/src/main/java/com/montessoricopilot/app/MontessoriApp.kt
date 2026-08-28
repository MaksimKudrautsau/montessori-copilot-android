package com.montessoricopilot.app

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.montessoricopilot.app.data.content.ContentDatabase
import com.montessoricopilot.app.data.user.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Application entry point.
 *
 * Starts the embedded Python interpreter once, for the process lifetime, and
 * lazily builds both local databases:
 *  - [ContentDatabase]: created empty by Room, then seeded once from the
 *    bundled assets/content_seed.json (read-only from then on)
 *  - [UserDatabase]: read/write, created on first launch, holds this
 *    household's actual data (children, journal, shelf)
 *
 * See design doc v0.2 ("Architecture") for why these are two separate Room
 * databases instead of one, and why Python only ever sees plain data.
 */
class MontessoriApp : Application() {

    /** Outlives any single screen — seeding content.db must finish even if
     *  the screen that triggered it has already navigated away. */
    val applicationScope = CoroutineScope(SupervisorJob())

    val contentDatabase: ContentDatabase by lazy { ContentDatabase.build(this, applicationScope) }
    val userDatabase: UserDatabase by lazy { UserDatabase.build(this) }

    override fun onCreate() {
        super.onCreate()
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }
}
