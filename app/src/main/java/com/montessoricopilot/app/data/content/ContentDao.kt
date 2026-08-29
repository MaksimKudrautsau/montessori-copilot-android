package com.montessoricopilot.app.data.content

import androidx.room.Dao
import androidx.room.Query

/**
 * All reads join the requested locale and English, then COALESCE field by
 * field. That means:
 *   - a fully translated activity shows in the user's language,
 *   - a partially translated one falls back per field rather than per row,
 *   - a missing translation still renders, in English, instead of vanishing.
 *
 * English is required to exist for every row; tests/test_seed_integrity.py
 * enforces that in the content pipeline.
 */
private const val ACTIVITY_COLUMNS = """
    a.id, a.ageMinMonths, a.ageMaxMonths, a.area, a.infantFocus,
    a.sessionMinutes, a.messLevel, a.provenance,
    a.imageAsset, a.imageCredit, a.imageLicence,
    COALESCE(loc.title, en.title) AS title,
    COALESCE(loc.summary, en.summary) AS summary,
    COALESCE(loc.whyItMatters, en.whyItMatters) AS whyItMatters,
    COALESCE(loc.howToPresent, en.howToPresent) AS howToPresent,
    COALESCE(loc.whatToObserve, en.whatToObserve) AS whatToObserve,
    COALESCE(loc.commonMistakes, en.commonMistakes) AS commonMistakes,
    COALESCE(loc.materialsNeeded, en.materialsNeeded) AS materialsNeeded,
    COALESCE(loc.homemadeAlternative, en.homemadeAlternative) AS homemadeAlternative,
    COALESCE(loc.supervisionNote, en.supervisionNote) AS supervisionNote
"""

private const val ACTIVITY_JOINS = """
    FROM activities a
    JOIN activity_texts en ON en.activityId = a.id AND en.locale = 'en'
    LEFT JOIN activity_texts loc ON loc.activityId = a.id AND loc.locale = :locale
"""

@Dao
interface ContentDao {

    // --- Activities -------------------------------------------------------

    @Query("SELECT $ACTIVITY_COLUMNS $ACTIVITY_JOINS ORDER BY title")
    suspend fun getAllActivities(locale: String): List<LocalizedActivity>

    @Query(
        "SELECT $ACTIVITY_COLUMNS $ACTIVITY_JOINS " +
            "WHERE :ageMonths BETWEEN a.ageMinMonths AND a.ageMaxMonths " +
            "ORDER BY a.area, title"
    )
    suspend fun getActivitiesForAge(locale: String, ageMonths: Int): List<LocalizedActivity>

    @Query(
        "SELECT $ACTIVITY_COLUMNS $ACTIVITY_JOINS " +
            "WHERE a.area = :area ORDER BY a.ageMinMonths, title"
    )
    suspend fun getActivitiesByArea(locale: String, area: String): List<LocalizedActivity>

    /** Searches the localised text and the English fallback, so a Russian user
     *  typing an English material name still finds it. */
    @Query(
        "SELECT $ACTIVITY_COLUMNS $ACTIVITY_JOINS " +
            "WHERE COALESCE(loc.title, en.title) LIKE '%' || :query || '%' " +
            "   OR COALESCE(loc.summary, en.summary) LIKE '%' || :query || '%' " +
            "   OR en.title LIKE '%' || :query || '%' " +
            "ORDER BY title"
    )
    suspend fun searchActivities(locale: String, query: String): List<LocalizedActivity>

    @Query("SELECT $ACTIVITY_COLUMNS $ACTIVITY_JOINS WHERE a.id = :activityId")
    suspend fun getActivity(locale: String, activityId: Int): LocalizedActivity?

    @Query("SELECT $ACTIVITY_COLUMNS $ACTIVITY_JOINS WHERE a.id IN (:ids)")
    suspend fun getActivitiesByIds(locale: String, ids: List<Int>): List<LocalizedActivity>

    /** Locale-independent projection for the Python recommendation engine —
     *  it only needs ids, ages and areas, so we skip the text joins entirely. */
    @Query("SELECT id, ageMinMonths, ageMaxMonths, area, infantFocus, sessionMinutes, messLevel, provenance, imageAsset, imageCredit, imageLicence FROM activities")
    suspend fun getActivityFacts(): List<ActivityEntity>

    // --- Sensitive periods -------------------------------------------------

    @Query(
        """
        SELECT p.id, p.ageMinMonths, p.ageMaxMonths,
               COALESCE(loc.name, en.name) AS name,
               COALESCE(loc.description, en.description) AS description,
               COALESCE(loc.whatYoullNotice, en.whatYoullNotice) AS whatYoullNotice,
               COALESCE(loc.howToSupport, en.howToSupport) AS howToSupport
        FROM sensitive_periods p
        JOIN sensitive_period_texts en ON en.periodId = p.id AND en.locale = 'en'
        LEFT JOIN sensitive_period_texts loc ON loc.periodId = p.id AND loc.locale = :locale
        ORDER BY p.ageMinMonths
        """
    )
    suspend fun getAllSensitivePeriods(locale: String): List<LocalizedSensitivePeriod>

    @Query(
        """
        SELECT p.id, p.ageMinMonths, p.ageMaxMonths,
               COALESCE(loc.name, en.name) AS name,
               COALESCE(loc.description, en.description) AS description,
               COALESCE(loc.whatYoullNotice, en.whatYoullNotice) AS whatYoullNotice,
               COALESCE(loc.howToSupport, en.howToSupport) AS howToSupport
        FROM sensitive_periods p
        JOIN sensitive_period_texts en ON en.periodId = p.id AND en.locale = 'en'
        LEFT JOIN sensitive_period_texts loc ON loc.periodId = p.id AND loc.locale = :locale
        WHERE :ageMonths BETWEEN p.ageMinMonths AND p.ageMaxMonths
        ORDER BY p.ageMinMonths
        """
    )
    suspend fun getActiveSensitivePeriods(locale: String, ageMonths: Int): List<LocalizedSensitivePeriod>

    /**
     * Active periods with BOTH their English name (what the Python matcher is
     * keyed on) and the localised name (what the user reads). Returning both in
     * one row is what lets a recommendation be matched in English and displayed
     * in Russian without a second lookup.
     */
    @Query(
        """
        SELECT p.id AS id,
               en.name AS nameEn,
               COALESCE(loc.name, en.name) AS nameLocalized
        FROM sensitive_periods p
        JOIN sensitive_period_texts en ON en.periodId = p.id AND en.locale = 'en'
        LEFT JOIN sensitive_period_texts loc ON loc.periodId = p.id AND loc.locale = :locale
        WHERE :ageMonths BETWEEN p.ageMinMonths AND p.ageMaxMonths
        ORDER BY p.ageMinMonths
        """
    )
    suspend fun getActivePeriodNames(locale: String, ageMonths: Int): List<PeriodNames>

    // --- Seeding check ------------------------------------------------------

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun activityCount(): Int
}
