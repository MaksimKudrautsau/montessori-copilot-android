package com.montessoricopilot.app.ui

import androidx.annotation.StringRes
import com.montessoricopilot.app.R
import com.montessoricopilot.app.data.content.Areas
import com.montessoricopilot.app.data.content.MessLevel
import com.montessoricopilot.app.data.content.Provenance

/**
 * Maps the stable string keys stored in the database to localised display
 * labels. The database deliberately stores language-neutral keys ("sensorial")
 * rather than display text, so the same row renders correctly in any language.
 */

@StringRes
fun areaLabel(area: String): Int = when (area) {
    Areas.PRACTICAL_LIFE -> R.string.area_practical_life
    Areas.SENSORIAL -> R.string.area_sensorial
    Areas.LANGUAGE -> R.string.area_language
    Areas.MATHEMATICS -> R.string.area_mathematics
    Areas.MOVEMENT -> R.string.area_movement
    Areas.ART_AND_MUSIC -> R.string.area_art_and_music
    Areas.GRACE_AND_COURTESY -> R.string.area_grace_and_courtesy
    Areas.CULTURE_AND_NATURE -> R.string.area_culture_and_nature
    // Unknown area means content newer than this build; show it as Practical
    // Life rather than crashing or rendering a raw key.
    else -> R.string.area_practical_life
}

@StringRes
fun messLevelLabel(messLevel: String): Int = when (messLevel) {
    MessLevel.NONE -> R.string.mess_none
    MessLevel.LOW -> R.string.mess_low
    MessLevel.MEDIUM -> R.string.mess_medium
    MessLevel.HIGH -> R.string.mess_high
    else -> R.string.mess_none
}

@StringRes
fun provenanceLabel(provenance: String): Int = when (provenance) {
    Provenance.MONTESSORI_PD -> R.string.provenance_montessori_pd
    else -> R.string.provenance_own_words
}
