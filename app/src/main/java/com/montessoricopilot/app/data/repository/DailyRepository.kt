package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.content.ContentDao
import com.montessoricopilot.app.data.content.LocalizedActivity
import com.montessoricopilot.app.data.user.UserDao
import com.montessoricopilot.app.logic.ActivityForPython
import com.montessoricopilot.app.logic.DailyFocusRequest
import com.montessoricopilot.app.logic.PeriodForPython
import com.montessoricopilot.app.logic.PythonBridge
import com.montessoricopilot.app.logic.UpcomingChangesRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** The single activity featured today, plus why it's timely. */
data class DailyFocus(
    val activity: LocalizedActivity,
    val reasonPeriodName: String?,
)

/**
 * What changes when the child reaches their next monthly milestone.
 * [daysAway] is how soon; the UI only shows this when it's close.
 */
data class UpcomingMilestone(
    val nextAgeMonths: Int,
    val daysAway: Long,
    val newlyEligible: List<LocalizedActivity>,
    val periodsStartingLocalized: List<String>,
    val periodsEndingLocalized: List<String>,
)

/** Show the milestone banner only inside this window. */
private const val MILESTONE_HORIZON_DAYS = 7L

class DailyRepository(
    private val contentDao: ContentDao,
    private val userDao: UserDao,
    private val contentRepository: ContentRepository,
) {

    suspend fun focusFor(
        childId: Int,
        ageMonths: Int,
        today: LocalDate = LocalDate.now(),
    ): DailyFocus? {
        val facts = contentDao.getActivityFacts()
        if (facts.isEmpty()) return null

        val activePeriods = contentRepository.activePeriodNames(ageMonths)
        val request = DailyFocusRequest(
            childId = childId,
            dayNumber = today.toEpochDay(),
            childAgeMonths = ageMonths,
            activities = facts.map {
                ActivityForPython(it.id, it.area, it.ageMinMonths, it.ageMaxMonths)
            },
            dismissedIds = userDao.getDismissedActivityIds(childId),
            activePeriodNames = activePeriods.map { it.nameEn },
        )

        val chosen = withContext(Dispatchers.Default) { PythonBridge.dailyFocus(request) }
            ?: return null

        val activity = contentRepository.activity(chosen.id) ?: return null
        val localizedByEn = activePeriods.associate { it.nameEn to it.nameLocalized }
        return DailyFocus(
            activity = activity,
            reasonPeriodName = chosen.reasonPeriodEn?.let { localizedByEn[it] },
        )
    }

    /**
     * The next monthly milestone, if it falls within the next week and
     * actually brings changes. Null the rest of the time — most months nothing
     * changes, and a banner saying so would be noise.
     *
     * Month arithmetic is done here rather than in Python because java.time
     * handles month lengths and end-of-month dates correctly: a child born on
     * the 31st still has a milestone in February.
     */
    suspend fun upcomingMilestone(
        birthDateEpochDay: Long,
        today: LocalDate = LocalDate.now(),
    ): UpcomingMilestone? {
        val birthDate = LocalDate.ofEpochDay(birthDateEpochDay)
        val currentAgeMonths = ChronoUnit.MONTHS.between(birthDate, today).toInt()
        val nextAgeMonths = currentAgeMonths + 1

        // plusMonths clamps to the last valid day, so a 31st birth date gives
        // 28 Feb rather than throwing.
        val milestoneDate = birthDate.plusMonths(nextAgeMonths.toLong())
        val daysAway = ChronoUnit.DAYS.between(today, milestoneDate)
        if (daysAway < 0 || daysAway > MILESTONE_HORIZON_DAYS) return null

        val facts = contentDao.getActivityFacts()
        // English rows carry the age bounds and the names the matcher keys on.
        val periodsEn = contentDao.getAllSensitivePeriods(locale = "en")

        val result = withContext(Dispatchers.Default) {
            PythonBridge.upcomingChanges(
                UpcomingChangesRequest(
                    currentAgeMonths = currentAgeMonths,
                    nextAgeMonths = nextAgeMonths,
                    activities = facts.map {
                        ActivityForPython(it.id, it.area, it.ageMinMonths, it.ageMaxMonths)
                    },
                    periods = periodsEn.map {
                        PeriodForPython(it.name, it.ageMinMonths, it.ageMaxMonths)
                    },
                )
            )
        }
        if (!result.hasChanges) return null

        // Period names came back in English; resolve to the user's language.
        // A single query returning both names, rather than zipping two
        // separately-ordered result sets — that would silently mistranslate
        // if either query's ordering ever changed.
        val localizedByEn = contentRepository.allPeriodNames()
            .associate { it.nameEn to it.nameLocalized }

        return UpcomingMilestone(
            nextAgeMonths = result.nextAgeMonths,
            daysAway = daysAway,
            newlyEligible = contentRepository.activitiesByIds(result.newlyEligibleIds),
            periodsStartingLocalized = result.periodsStarting.map { localizedByEn[it] ?: it },
            periodsEndingLocalized = result.periodsEnding.map { localizedByEn[it] ?: it },
        )
    }
}
