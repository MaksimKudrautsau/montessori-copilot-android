package com.montessoricopilot.app.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire types for the Kotlin <-> Python JSON boundary (see logic/bridge.py).
 * Field names are snake_case via @SerialName because that's the contract
 * bridge.py's payloads use — Python-side dict keys, not Kotlin convention.
 *
 * Note these carry **no localised text**. The recommendation engine works on
 * ids, ages and curriculum areas only; titles are resolved from Room after the
 * ids come back, which keeps the bridge payload small and language-agnostic.
 */

@Serializable
data class ActivityForPython(
    val id: Int,
    /** One of Areas.* — see data/content/ContentEntities.kt. */
    val area: String,
    @SerialName("age_min_months") val ageMinMonths: Int,
    @SerialName("age_max_months") val ageMaxMonths: Int,
)

@Serializable
data class RecommendRequest(
    @SerialName("child_age_months") val childAgeMonths: Int,
    val activities: List<ActivityForPython>,
    @SerialName("dismissed_ids") val dismissedIds: List<Int> = emptyList(),
    /** English sensitive-period names — the Python lookup table is keyed on
     *  English by design, and the localised name is resolved for display. */
    @SerialName("active_period_names") val activePeriodNames: List<String> = emptyList(),
    val limit: Int = 10,
)

@Serializable
data class RecommendedActivityResult(
    val id: Int,
    val area: String,
    @SerialName("age_min_months") val ageMinMonths: Int,
    @SerialName("age_max_months") val ageMaxMonths: Int,
    /**
     * The ENGLISH name of an active sensitive period this activity's area
     * serves, or null. Deliberately a bare name rather than a sentence: the
     * matching table is keyed on English, but the displayed wording is built
     * from a string resource so a Russian user reads Russian.
     */
    @SerialName("reason_period") val reasonPeriodEn: String? = null,
)

// --- Daily loop (PRD v0.5 E3) -----------------------------------------------

@Serializable
data class DailyFocusRequest(
    @SerialName("child_id") val childId: Int,
    /** LocalDate.toEpochDay() — the rotation is seeded on this, so the same
     *  child sees the same activity all day and a new one tomorrow. */
    @SerialName("day_number") val dayNumber: Long,
    @SerialName("child_age_months") val childAgeMonths: Int,
    val activities: List<ActivityForPython>,
    @SerialName("dismissed_ids") val dismissedIds: List<Int> = emptyList(),
    @SerialName("active_period_names") val activePeriodNames: List<String> = emptyList(),
)

@Serializable
data class PeriodForPython(
    /** English name — the matcher's table is keyed on English. */
    val name: String,
    @SerialName("age_min_months") val ageMinMonths: Int,
    @SerialName("age_max_months") val ageMaxMonths: Int,
)

@Serializable
data class UpcomingChangesRequest(
    @SerialName("current_age_months") val currentAgeMonths: Int,
    @SerialName("next_age_months") val nextAgeMonths: Int,
    val activities: List<ActivityForPython>,
    val periods: List<PeriodForPython>,
)

@Serializable
data class UpcomingChangesResult(
    @SerialName("next_age_months") val nextAgeMonths: Int,
    @SerialName("newly_eligible_ids") val newlyEligibleIds: List<Int> = emptyList(),
    @SerialName("periods_starting") val periodsStarting: List<String> = emptyList(),
    @SerialName("periods_ending") val periodsEnding: List<String> = emptyList(),
    /** False in most months; the UI shows no banner at all rather than an
     *  empty one. */
    @SerialName("has_changes") val hasChanges: Boolean = false,
)

@Serializable
data class ShelfItemForPython(
    val id: Int,
    val status: String,
    @SerialName("date_placed_epoch_day") val datePlacedEpochDay: Long,
)

@Serializable
data class RotationRequest(
    @SerialName("shelf_items") val shelfItems: List<ShelfItemForPython>,
    @SerialName("today_epoch_day") val todayEpochDay: Long,
    @SerialName("min_days_active") val minDaysActive: Int = 14,
)

@Serializable
data class ShelfRotationResult(
    val id: Int,
    val status: String,
    @SerialName("date_placed_epoch_day") val datePlacedEpochDay: Long,
    @SerialName("due_for_rotation") val dueForRotation: Boolean,
    @SerialName("days_on_shelf") val daysOnShelf: Long? = null,
)
