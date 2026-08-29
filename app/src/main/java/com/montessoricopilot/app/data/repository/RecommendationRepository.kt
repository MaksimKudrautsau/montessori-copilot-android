package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.content.ContentDao
import com.montessoricopilot.app.data.content.LocalizedActivity
import com.montessoricopilot.app.data.user.DismissedRecommendationEntity
import com.montessoricopilot.app.data.user.UserDao
import com.montessoricopilot.app.logic.ActivityForPython
import com.montessoricopilot.app.logic.PythonBridge
import com.montessoricopilot.app.logic.RecommendRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * An activity to recommend, plus the sensitive period that makes it timely.
 *
 * [reasonPeriodName] is already localised; the surrounding sentence is built
 * in the UI from a string resource, so no English leaks into a Russian screen.
 */
data class Recommendation(
    val activity: LocalizedActivity,
    val reasonPeriodName: String?,
)

/**
 * Bridges Room to the Python recommendation engine. The only repository that
 * touches both databases, since a recommendation depends on the content
 * library *and* on this child's own dismissal history.
 *
 * Flow: locale-independent facts go to Python → Python returns ordered ids →
 * localised text is fetched for just those ids. Language never crosses the
 * bridge.
 */
class RecommendationRepository(
    private val contentDao: ContentDao,
    private val userDao: UserDao,
    private val contentRepository: ContentRepository,
) {
    suspend fun recommendationsFor(
        childId: Int,
        ageMonths: Int,
        limit: Int = 10,
    ): List<Recommendation> {
        val facts = contentDao.getActivityFacts()
        val dismissedIds = userDao.getDismissedActivityIds(childId)
        val activePeriods = contentRepository.activePeriodNames(ageMonths)

        val request = RecommendRequest(
            childAgeMonths = ageMonths,
            activities = facts.map {
                ActivityForPython(it.id, it.area, it.ageMinMonths, it.ageMaxMonths)
            },
            dismissedIds = dismissedIds,
            // English names only — the matcher's table is keyed on English.
            activePeriodNames = activePeriods.map { it.nameEn },
            limit = limit,
        )

        // PythonBridge is a blocking native (Chaquopy/JNI) call — never let it
        // run on the caller's dispatcher, which for a ViewModel is Main.
        val ranked = withContext(Dispatchers.Default) { PythonBridge.recommend(request) }
        if (ranked.isEmpty()) return emptyList()

        // One query for the text of exactly the recommended ids, then restore
        // Python's ordering (SQL IN does not preserve it).
        val byId = contentRepository.activitiesByIds(ranked.map { it.id }).associateBy { it.id }

        // Translate the English period name Python matched on into the name
        // the user should actually read.
        val localizedByEn = activePeriods.associate { it.nameEn to it.nameLocalized }

        return ranked.mapNotNull { result ->
            byId[result.id]?.let { activity ->
                Recommendation(
                    activity = activity,
                    reasonPeriodName = result.reasonPeriodEn?.let { localizedByEn[it] },
                )
            }
        }
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
