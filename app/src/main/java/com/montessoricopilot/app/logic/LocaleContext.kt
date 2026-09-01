package com.montessoricopilot.app.logic

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.montessoricopilot.app.data.prefs.LanguagePreference
import java.util.Locale

/**
 * Applies the user's chosen language to a Context.
 *
 * Two things have to follow the choice, and this handles both at once:
 *
 *  1. **UI strings** — by returning a Context whose Resources are configured
 *     for the chosen locale. Compose's `stringResource` reads from
 *     `LocalContext`, which descends from the activity's base context, so
 *     overriding it there covers every screen.
 *
 *  2. **Database content** — `ContentRepository` picks its locale from
 *     `Locale.getDefault()`. Setting the process default here means activity
 *     titles and activity *text* stay in step without any extra plumbing.
 *
 * Deliberately not using AppCompatDelegate.setApplicationLocales: that needs
 * androidx.appcompat, an AppCompatActivity, and an AppCompat parent theme.
 * This app draws entirely in Compose and uses ComponentActivity, so pulling in
 * AppCompat and swapping the theme would be a lot of moving parts — and a
 * theme mismatch there crashes at startup rather than failing to compile.
 */
fun Context.withAppLanguage(): Context {
    val choice = LanguagePreference.get(this)

    val locale = if (choice == LanguagePreference.SYSTEM) {
        // Read the *device* locale, not Locale.getDefault(): the default may
        // already have been overridden earlier in this process, so switching
        // back to "System" would otherwise keep the previous override.
        systemLocale()
    } else {
        Locale(choice)
    }

    Locale.setDefault(locale)

    val configuration = Configuration(resources.configuration).apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
    return createConfigurationContext(configuration)
}

/** The device's own locale, unaffected by anything this app has set. */
private fun systemLocale(): Locale {
    val systemConfig = Resources.getSystem().configuration
    return systemConfig.locales.takeIf { !it.isEmpty }?.get(0) ?: Locale.ENGLISH
}
