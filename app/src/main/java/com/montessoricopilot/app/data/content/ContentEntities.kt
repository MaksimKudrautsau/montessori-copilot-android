package com.montessoricopilot.app.data.content

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Content schema v2 (PRD v0.5 P0).
 *
 * Split into locale-independent facts ([ActivityEntity]) and localised text
 * ([ActivityTextEntity]) so that adding a language never duplicates ages,
 * curriculum areas or image credits — and so the recommendation engine, which
 * only needs ids/ages/areas, never has to care about language at all.
 *
 * Authored by tools/generate_content_seed.py and seeded once on first launch.
 * Never written to at runtime.
 */

/** The eight classic Montessori curriculum areas, as stored in [ActivityEntity.area]. */
object Areas {
    const val PRACTICAL_LIFE = "practical_life"
    const val SENSORIAL = "sensorial"
    const val LANGUAGE = "language"
    const val MATHEMATICS = "mathematics"
    const val MOVEMENT = "movement"
    const val ART_AND_MUSIC = "art_and_music"
    const val GRACE_AND_COURTESY = "grace_and_courtesy"
    const val CULTURE_AND_NATURE = "culture_and_nature"

    /** Display order for filters and grouping — curriculum order, not alphabetical. */
    val ALL = listOf(
        PRACTICAL_LIFE, SENSORIAL, LANGUAGE, MATHEMATICS,
        MOVEMENT, ART_AND_MUSIC, GRACE_AND_COURTESY, CULTURE_AND_NATURE,
    )
}

/** Secondary axis for roughly 0–12 months, where the classic areas assume a
 *  mobile child and don't discriminate usefully. Null for older activities. */
object InfantFocus {
    const val VISUAL_DEVELOPMENT = "visual_development"
    const val AUDITORY = "auditory"
    const val GRASPING = "grasping"
    const val GROSS_MOTOR_INFANT = "gross_motor_infant"
}

/** How much cleanup an activity implies — often the deciding factor on a
 *  weekday evening, so it's a first-class field rather than buried in prose. */
object MessLevel {
    const val NONE = "none"
    const val LOW = "low"
    const val MEDIUM = "medium"
    const val HIGH = "high"
}

/** Where this content came from. Required on every activity — see PRD v0.5 §6. */
object Provenance {
    /** Written for this app from general knowledge of the method. */
    const val OWN_WORDS = "own_words"

    /** Derived from Maria Montessori's public-domain writing. */
    const val MONTESSORI_PD = "montessori_pd"
}

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: Int,
    val ageMinMonths: Int,
    val ageMaxMonths: Int,
    /** One of [Areas]. */
    val area: String,
    /** One of [InfantFocus], or null for activities above ~12 months. */
    val infantFocus: String?,
    val sessionMinutes: Int,
    /** One of [MessLevel]. */
    val messLevel: String,
    /** One of [Provenance]. */
    val provenance: String,
    val imageAsset: String?,
    val imageCredit: String?,
    val imageLicence: String?,
)

@Entity(
    tableName = "activity_texts",
    primaryKeys = ["activityId", "locale"],
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("activityId")],
)
data class ActivityTextEntity(
    val activityId: Int,
    /** ISO 639-1 language code: "en", "ru". English must always exist — it is
     *  the fallback every query falls back to. */
    val locale: String,
    val title: String,
    val summary: String,
    val whyItMatters: String,
    val howToPresent: String,
    val whatToObserve: String,
    val commonMistakes: String,
    val materialsNeeded: String,
    val homemadeAlternative: String,
    val supervisionNote: String?,
)

@Entity(tableName = "sensitive_periods")
data class SensitivePeriodEntity(
    @PrimaryKey val id: Int,
    val ageMinMonths: Int,
    val ageMaxMonths: Int,
)

@Entity(
    tableName = "sensitive_period_texts",
    primaryKeys = ["periodId", "locale"],
    foreignKeys = [
        ForeignKey(
            entity = SensitivePeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["periodId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("periodId")],
)
data class SensitivePeriodTextEntity(
    val periodId: Int,
    val locale: String,
    val name: String,
    val description: String,
    val whatYoullNotice: String,
    val howToSupport: String,
)

/**
 * A query result joining an activity to its text in the requested locale,
 * falling back field-by-field to English. Not a table — Room builds this from
 * the SELECT in [ContentDao].
 */
data class LocalizedActivity(
    val id: Int,
    val ageMinMonths: Int,
    val ageMaxMonths: Int,
    val area: String,
    val infantFocus: String?,
    val sessionMinutes: Int,
    val messLevel: String,
    val provenance: String,
    val imageAsset: String?,
    val imageCredit: String?,
    val imageLicence: String?,
    val title: String,
    val summary: String,
    val whyItMatters: String,
    val howToPresent: String,
    val whatToObserve: String,
    val commonMistakes: String,
    val materialsNeeded: String,
    val homemadeAlternative: String,
    val supervisionNote: String?,
)

/**
 * A sensitive period's name in both English and the user's language.
 *
 * The pair exists because matching and display have different needs: the
 * Python lookup table is keyed on stable English names, while the user must
 * read their own language. Carrying both avoids a second query.
 */
data class PeriodNames(
    val id: Int,
    val nameEn: String,
    val nameLocalized: String,
)

/** As [LocalizedActivity], for sensitive periods. */
data class LocalizedSensitivePeriod(
    val id: Int,
    val ageMinMonths: Int,
    val ageMaxMonths: Int,
    val name: String,
    val description: String,
    val whatYoullNotice: String,
    val howToSupport: String,
)
