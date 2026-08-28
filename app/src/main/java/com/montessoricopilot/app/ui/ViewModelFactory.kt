package com.montessoricopilot.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** Minimal manual DI: no Hilt/Dagger dependency (keeps the project free of
 *  extra build-time complexity while it's still small) — just a factory
 *  that runs a lambda to construct whichever ViewModel a screen needs. */
class ViewModelFactory(private val creator: () -> ViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator() as T
}
