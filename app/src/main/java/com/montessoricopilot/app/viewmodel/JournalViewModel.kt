package com.montessoricopilot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montessoricopilot.app.data.repository.JournalRepository
import com.montessoricopilot.app.data.user.JournalEntryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(
    private val childId: Int,
    private val journalRepository: JournalRepository,
) : ViewModel() {

    val entries: StateFlow<List<JournalEntryEntity>> =
        journalRepository.observeForChild(childId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addEntry(category: String, note: String) {
        if (note.isBlank()) return
        viewModelScope.launch { journalRepository.addEntry(childId, category, note) }
    }
}
