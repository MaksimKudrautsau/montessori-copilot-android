package com.montessoricopilot.app.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.prefs.LanguagePreference

/**
 * Language switcher for the whole app.
 *
 * Changing the language recreates the activity. That is not laziness — the
 * locale is baked into the Context's Resources at `attachBaseContext`, so
 * every already-composed `stringResource` and every already-loaded database
 * row would otherwise keep the old language. Recreating is how Android itself
 * handles a locale change, and it is instant.
 */
@Composable
fun LanguageMenu() {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val current = remember { LanguagePreference.get(context) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            Icons.Filled.Language,
            contentDescription = stringResource(R.string.language),
        )
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        LanguagePreference.OPTIONS.forEach { option ->
            DropdownMenuItem(
                text = { Text(stringResource(languageLabel(option))) },
                leadingIcon = {
                    if (option == current) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }
                },
                onClick = {
                    expanded = false
                    if (option != current) {
                        LanguagePreference.set(context, option)
                        context.findActivity()?.recreate()
                    }
                },
            )
        }
    }
}

private fun languageLabel(option: String): Int = when (option) {
    LanguagePreference.ENGLISH -> R.string.language_english
    LanguagePreference.RUSSIAN -> R.string.language_russian
    else -> R.string.language_system
}

/**
 * Compose's LocalContext can be a ContextWrapper rather than the Activity
 * itself, so unwrap rather than casting — a bare cast crashes in exactly the
 * cases where it matters (previews, wrapped contexts).
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
