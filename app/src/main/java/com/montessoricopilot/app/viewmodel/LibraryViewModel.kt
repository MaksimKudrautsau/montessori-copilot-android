package com.montessoricopilot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montessoricopilot.app.data.content.LocalizedActivity
import com.montessoricopilot.app.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryUiState(
    val query: String = "",
    /** Null means "all areas". */
    val selectedArea: String? = null,
    val activities: List<LocalizedActivity> = emptyList(),
    val isLoading: Boolean = true,
)

class LibraryViewModel(private val contentRepository: ContentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init { reload() }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        reload()
    }

    fun onAreaSelected(area: String?) {
        _uiState.value = _uiState.value.copy(selectedArea = area)
        reload()
    }

    private fun reload() {
        viewModelScope.launch {
            val state = _uiState.value
            val results = when {
                state.query.isNotBlank() -> contentRepository.search(state.query)
                state.selectedArea != null -> contentRepository.activitiesByArea(state.selectedArea)
                else -> contentRepository.allActivities()
            }
            // Area filter also applies on top of a search, so the two controls
            // compose rather than overriding each other.
            val filtered = if (state.query.isNotBlank() && state.selectedArea != null) {
                results.filter { it.area == state.selectedArea }
            } else {
                results
            }
            _uiState.value = _uiState.value.copy(activities = filtered, isLoading = false)
        }
    }
}
