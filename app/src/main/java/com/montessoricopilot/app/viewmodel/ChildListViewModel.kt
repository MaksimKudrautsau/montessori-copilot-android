package com.montessoricopilot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montessoricopilot.app.data.repository.ChildRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChildListViewModel(private val childRepository: ChildRepository) : ViewModel() {

    val children: StateFlow<List<com.montessoricopilot.app.data.user.ChildEntity>> =
        childRepository.observeChildren()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addChild(name: String, birthDate: LocalDate) {
        viewModelScope.launch { childRepository.addChild(name, birthDate) }
    }
}
