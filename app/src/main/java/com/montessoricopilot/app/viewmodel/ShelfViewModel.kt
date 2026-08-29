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

/**
 * A shelf item joined with its resolved title and rotation-due status. The
 * view doesn't need to know that the title comes from Room (in the user's
 * language) while the rotation status comes from Python.
 */
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
    /** Localised "Untitled item", resolved by the caller — a ViewModel has no
     *  Context and should not be reaching for string resources itself. */
    private val fallbackTitle: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shelfRepository.observeForChild(childId).collectLatest { items -> refresh(items) }
        }
    }

    private suspend fun refresh(items: List<ShelfItemEntity>) {
        // Titles for library-linked items come back in the user's language.
        val titles = contentRepository
            .activitiesByIds(items.mapNotNull { it.activityId })
            .associateBy { it.id }

        val rotationById = shelfRepository.rotationStatus(items).associateBy { it.id }

        val enriched = items.map { item ->
            val title = item.customTitle
                ?: titles[item.activityId]?.title
                ?: fallbackTitle
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
        viewModelScope.launch {
            shelfRepository.addItem(
                childId = childId, activityId = null, customTitle = title, active = true,
            )
        }
    }

    fun moveToStorage(item: ShelfItemEntity) {
        viewModelScope.launch { shelfRepository.moveToStorage(item) }
    }

    fun moveToActiveShelf(item: ShelfItemEntity) {
        viewModelScope.launch { shelfRepository.moveToActiveShelf(item) }
    }
}
