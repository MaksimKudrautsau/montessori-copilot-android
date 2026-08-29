package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.content.ContentDao
import com.montessoricopilot.app.data.content.LocalizedActivity
import com.montessoricopilot.app.data.content.LocalizedSensitivePeriod
import com.montessoricopilot.app.data.content.PeriodNames
import java.util.Locale

/**
 * Content reads, resolved into the user's language.
 *
 * The locale is read from the system on each call rather than cached, so the
 * app follows an Android 13+ per-app language change without a restart.
 * Content falls back to English field-by-field (see ContentDao), so an
 * untranslated field never renders blank.
 *
 * Only "en" and "ru" have content today; anything else lands on English.
 */
class ContentRepository(private val contentDao: ContentDao) {

    private val supportedLocales = setOf("en", "ru")

    private fun currentLocale(): String {
        val language = Locale.getDefault().language
        return if (language in supportedLocales) language else "en"
    }

    suspend fun allActivities(): List<LocalizedActivity> =
        contentDao.getAllActivities(currentLocale())

    suspend fun activitiesForAge(ageMonths: Int): List<LocalizedActivity> =
        contentDao.getActivitiesForAge(currentLocale(), ageMonths)

    suspend fun activitiesByArea(area: String): List<LocalizedActivity> =
        contentDao.getActivitiesByArea(currentLocale(), area)

    suspend fun search(query: String): List<LocalizedActivity> =
        contentDao.searchActivities(currentLocale(), query)

    suspend fun activity(activityId: Int): LocalizedActivity? =
        contentDao.getActivity(currentLocale(), activityId)

    suspend fun activitiesByIds(ids: List<Int>): List<LocalizedActivity> =
        if (ids.isEmpty()) emptyList() else contentDao.getActivitiesByIds(currentLocale(), ids)

    suspend fun activeSensitivePeriods(ageMonths: Int): List<LocalizedSensitivePeriod> =
        contentDao.getActiveSensitivePeriods(currentLocale(), ageMonths)

    /** Active periods with both English and localised names — see [PeriodNames]. */
    suspend fun activePeriodNames(ageMonths: Int): List<PeriodNames> =
        contentDao.getActivePeriodNames(currentLocale(), ageMonths)

    suspend fun allSensitivePeriods(): List<LocalizedSensitivePeriod> =
        contentDao.getAllSensitivePeriods(currentLocale())
}
