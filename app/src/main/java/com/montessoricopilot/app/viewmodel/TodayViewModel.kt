package com.montessoricopilot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montessoricopilot.app.data.content.LocalizedSensitivePeriod
import com.montessoricopilot.app.data.repository.ChildRepository
import com.montessoricopilot.app.data.repository.ContentRepository
import com.montessoricopilot.app.data.repository.DailyFocus
import com.montessoricopilot.app.data.repository.DailyRepository
import com.montessoricopilot.app.data.repository.Recommendation
import com.montessoricopilot.app.data.repository.RecommendationRepository
import com.montessoricopilot.app.data.repository.ShelfRepository
import com.montessoricopilot.app.data.repository.UpcomingMilestone
import com.montessoricopilot.app.data.user.ChildEntity
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
    /** The single featured activity for today. Null only when nothing is
     *  age-eligible, which in practice means every option was dismissed. */
    val focus: DailyFocus? = null,
    /** Non-null only in the week before a monthly milestone that actually
     *  brings changes. */
    val milestone: UpcomingMilestone? = null,
    val recommendations: List<Recommendation> = emptyList(),
    val activePeriods: List<LocalizedSensitivePeriod> = emptyList(),
    val itemsDueForRotation: Int = 0,
)

class TodayViewModel(
    private val childId: Int,
    private val childRepository: ChildRepository,
    private val contentRepository: ContentRepository,
    private val recommendationRepository: RecommendationRepository,
    private val shelfRepository: ShelfRepository,
    private val dailyRepository: DailyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val child = childRepository.getChild(childId) ?: return@launch
            val age = ageInMonths(child.birthDateEpochDay)

            val focus = dailyRepository.focusFor(childId, age)
            val milestone = dailyRepository.upcomingMilestone(child.birthDateEpochDay)
            val activePeriods = contentRepository.activeSensitivePeriods(age)

            // Today's focus is shown separately above, so drop it from the
            // list below rather than showing the same card twice.
            val recommendations = recommendationRepository
                .recommendationsFor(childId, age)
                .filterNot { it.activity.id == focus?.activity?.id }

            val shelfItems = shelfRepository.observeForChild(childId).first()
            val dueCount = shelfRepository.rotationStatus(shelfItems).count { it.dueForRotation }

            _uiState.value = TodayUiState(
                isLoading = false,
                child = child,
                ageMonths = age,
                focus = focus,
                milestone = milestone,
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
