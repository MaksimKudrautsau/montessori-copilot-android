package com.montessoricopilot.app.data.prefs

import android.content.Context

/**
 * The user's chosen app language.
 *
 * Stored in SharedPreferences rather than DataStore on purpose: this value has
 * to be read **synchronously inside `Activity.attachBaseContext`**, before the
 * activity's resources are created. DataStore is asynchronous, and calling
 * `runBlocking` there would block the main thread during startup for no gain.
 * A single string is exactly what SharedPreferences is for.
 */
object LanguagePreference {

    /** Follow whatever the device is set to. The default. */
    const val SYSTEM = "system"
    const val ENGLISH = "en"
    const val RUSSIAN = "ru"

    /** Offered in the switcher, in this order. */
    val OPTIONS = listOf(SYSTEM, ENGLISH, RUSSIAN)

    private const val PREFS_NAME = "settings"
    private const val KEY_LANGUAGE = "app_language"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** The stored choice, or [SYSTEM] if none has been made. */
    fun get(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, SYSTEM) ?: SYSTEM

    fun set(context: Context, language: String) {
        require(language in OPTIONS) { "unsupported language: $language" }
        // commit(), not apply(): the activity is recreated immediately after
        // this call, and attachBaseContext must see the new value. apply() is
        // asynchronous and would race with the recreate.
        prefs(context).edit().putString(KEY_LANGUAGE, language).commit()
    }
}
