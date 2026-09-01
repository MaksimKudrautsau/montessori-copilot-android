package com.montessoricopilot.app.data.prefs

import android.content.Context

/**
 * Whether the weekly reminder is on. **Off by default** — an app that starts
 * notifying without being asked is exactly the kind of attention-grabbing the
 * PRD's third design principle rules out.
 */
object ReminderPreference {

    private const val PREFS_NAME = "settings"
    private const val KEY_WEEKLY_REMINDER = "weekly_reminder_enabled"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_WEEKLY_REMINDER, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_WEEKLY_REMINDER, enabled).apply()
    }
}
