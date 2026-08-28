package com.montessoricopilot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montessoricopilot.app.data.content.ActivityEntity
import com.montessoricopilot.app.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryUiState(
    val query: String = "",
    val activities: List<ActivityEntity> = emptyList(),
    val isLoading: Boolean = true,
)

class LibraryViewModel(private val contentRepository: ContentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init { loadAll() }

    private fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(activities = contentRepository.allActivities(), isLoading = false)
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        viewModelScope.launch {
            val results = if (query.isBlank()) contentRepository.allActivities() else contentRepository.search(query)
            _uiState.value = _uiState.value.copy(activities = results)
        }
    }
}
