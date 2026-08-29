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
