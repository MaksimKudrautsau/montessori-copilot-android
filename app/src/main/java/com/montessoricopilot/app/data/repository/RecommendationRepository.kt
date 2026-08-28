package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.content.ContentDao
import com.montessoricopilot.app.data.user.DismissedRecommendationEntity
import com.montessoricopilot.app.data.user.UserDao
import com.montessoricopilot.app.logic.ActivityForPython
import com.montessoricopilot.app.logic.PythonBridge
import com.montessoricopilot.app.logic.RecommendRequest
import com.montessoricopilot.app.logic.RecommendedActivityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Bridges Room (content + user data) to the Python recommendation engine.
 * This is the only repository that talks to both databases, since a
 * recommendation genuinely depends on both the content library and this
 * child's own history (dismissed activities).
 */
class RecommendationRepository(
    private val contentDao: ContentDao,
    private val userDao: UserDao,
) {
    suspend fun recommendationsFor(childId: Int, ageMonths: Int, limit: Int = 10): List<RecommendedActivityResult> {
        val allActivities = contentDao.getAllActivities()
        val dismissedIds = userDao.getDismissedActivityIds(childId)
        val activePeriods = contentDao.getActiveSensitivePeriods(ageMonths).map { it.periodName }

        val request = RecommendRequest(
            childAgeMonths = ageMonths,
            activities = allActivities.map {
                ActivityForPython(it.id, it.title, it.category, it.ageMinMonths, it.ageMaxMonths)
            },
            dismissedIds = dismissedIds,
            activePeriodNames = activePeriods,
            limit = limit,
        )
        // PythonBridge is a blocking native (Chaquopy/JNI) call — never let
        // it run on the caller's dispatcher, which for a ViewModel's
        // viewModelScope is Main. Default is right for this: it's CPU-bound
        // filtering/sorting, not I/O.
        return withContext(Dispatchers.Default) { PythonBridge.recommend(request) }
    }

    /** Parent said "not now" — stop resurfacing this activity for this child. */
    suspend fun dismiss(childId: Int, activityId: Int) {
        userDao.insertDismissed(
            DismissedRecommendationEntity(
                childId = childId,
                activityId = activityId,
                dismissedAtEpochMillis = Instant.now().toEpochMilli(),
            )
        )
    }
}
