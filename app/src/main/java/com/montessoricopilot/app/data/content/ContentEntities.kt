package com.montessoricopilot.app.data.content

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * One row per curated Montessori activity. Authored offline as JSON (see
 * /tools/generate_content_seed.py -> assets/content_seed.json) and inserted
 * once, the first time the app runs, by ContentDatabase's seed callback —
 * never written to again at runtime. Age bounds are inclusive, in months.
 *
 * @Serializable is used only to parse the bundled JSON seed on first launch;
 * it has no bearing on how Room persists the table.
 */
@Serializable
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val ageMinMonths: Int,
    val ageMaxMonths: Int,
    val category: String, // one of Category.name in the logic layer
    val materialsNeeded: String,
    val preparedEnvironmentTips: String,
)

/**
 * Classic Montessori sensitive periods, each active over an age range.
 * Also authored offline as JSON, seeded alongside [ActivityEntity].
 */
@Serializable
@Entity(tableName = "sensitive_periods")
data class SensitivePeriodEntity(
    @PrimaryKey val id: Int,
    val periodName: String,
    val description: String,
    val ageMinMonths: Int,
    val ageMaxMonths: Int,
)
