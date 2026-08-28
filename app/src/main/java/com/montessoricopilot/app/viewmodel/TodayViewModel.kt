package com.montessoricopilot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montessoricopilot.app.data.content.SensitivePeriodEntity
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.data.user.ChildEntity
import com.montessoricopilot.app.logic.RecommendedActivityResult
import com.montessoricopilot.app.logic.ShelfRotationResult
import com.montessoricopilot.app.logic.ageInMonths
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class TodayUiState(
    val isLoading: Boolean = true,
    val child: ChildEntity? = null,
    val ageMonths: Int = 0,
    val recommendations: List<RecommendedActivityResult> = emptyList(),
    val activePeriods: List<SensitivePeriodEntity> = emptyList(),
    val itemsDueForRotation: Int = 0,
)

class TodayViewModel(
    private val childId: Int,
    private val childRepository: ChildRepository,
    private val contentRepository: ContentRepository,
    private val recommendationRepository: RecommendationRepository,
    private val shelfRepository: ShelfRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val child = childRepository.getChild(childId) ?: return@launch
            val age = ageInMonths(child.birthDateEpochDay)

            val recommendations = recommendationRepository.recommendationsFor(childId, age)
            val activePeriods = contentRepository.activeSensitivePeriods(age)

            val shelfItems = shelfRepository.observeForChild(childId).first()
            val rotationStatuses: List<ShelfRotationResult> = shelfRepository.rotationStatus(shelfItems)
            val dueCount = rotationStatuses.count { it.dueForRotation }

            _uiState.value = TodayUiState(
                isLoading = false,
                child = child,
                ageMonths = age,
                recommendations = recommendations,
                activePeriods = activePeriods,
                itemsDueForRotation = dueCount,
            )
        }
    }

    fun dismissRecommendation(activityId: Int) {
        viewModelScope.launch {
            recommendationRepository.dismiss(childId, activityId)
            refresh()
        }
    }
}
