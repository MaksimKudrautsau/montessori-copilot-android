package com.montessoricopilot.app.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire types for the Kotlin <-> Python JSON boundary (see logic/bridge.py).
 * Field names are snake_case via @SerialName because that's the contract
 * bridge.py's payloads use — Python-side dict keys, not Kotlin convention.
 * Kept separate from the Room entities in data/content and data/user so
 * either side can evolve without silently breaking the other.
 */

@Serializable
data class ActivityForPython(
    val id: Int,
    val title: String,
    val category: String,
    @SerialName("age_min_months") val ageMinMonths: Int,
    @SerialName("age_max_months") val ageMaxMonths: Int,
)

@Serializable
data class RecommendRequest(
    @SerialName("child_age_months") val childAgeMonths: Int,
    val activities: List<ActivityForPython>,
    @SerialName("dismissed_ids") val dismissedIds: List<Int> = emptyList(),
    @SerialName("active_period_names") val activePeriodNames: List<String> = emptyList(),
    val limit: Int = 10,
)

@Serializable
data class RecommendedActivityResult(
    val id: Int,
    val title: String,
    val category: String,
    @SerialName("age_min_months") val ageMinMonths: Int,
    @SerialName("age_max_months") val ageMaxMonths: Int,
    val reason: String? = null,
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
