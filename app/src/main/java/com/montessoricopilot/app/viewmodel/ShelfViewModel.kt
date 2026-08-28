package com.montessoricopilot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.data.user.ShelfItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** A shelf item joined with its resolved title and rotation-due status —
 *  the view doesn't need to know that title resolution and rotation
 *  status come from two different sources (Room + Python). */
data class ShelfItemUi(
    val item: ShelfItemEntity,
    val title: String,
    val dueForRotation: Boolean,
    val daysOnShelf: Long?,
)

data class ShelfUiState(
    val active: List<ShelfItemUi> = emptyList(),
    val storage: List<ShelfItemUi> = emptyList(),
    val isLoading: Boolean = true,
)

class ShelfViewModel(
    private val childId: Int,
    private val shelfRepository: ShelfRepository,
    private val contentRepository: ContentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shelfRepository.observeForChild(childId).collectLatest { items -> refresh(items) }
        }
    }

    private suspend fun refresh(items: List<ShelfItemEntity>) {
        val activityTitles = contentRepository
            .activitiesByIds(items.mapNotNull { it.activityId })
            .associateBy { it.id }

        val rotationById = shelfRepository.rotationStatus(items).associateBy { it.id }

        val enriched = items.map { item ->
            val title = item.customTitle ?: activityTitles[item.activityId]?.title ?: "Untitled item"
            val rotation = rotationById[item.id]
            ShelfItemUi(item, title, rotation?.dueForRotation ?: false, rotation?.daysOnShelf)
        }

        _uiState.value = ShelfUiState(
            active = enriched.filter { it.item.status == "active" },
            storage = enriched.filter { it.item.status == "storage" },
            isLoading = false,
        )
    }

    fun addCustomItem(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { shelfRepository.addItem(childId, activityId = null, customTitle = title, active = true) }
    }

    fun moveToStorage(item: ShelfItemEntity) {
        viewModelScope.launch { shelfRepository.moveToStorage(item) }
    }

    fun moveToActiveShelf(item: ShelfItemEntity) {
        viewModelScope.launch { shelfRepository.moveToActiveShelf(item) }
    }
}
