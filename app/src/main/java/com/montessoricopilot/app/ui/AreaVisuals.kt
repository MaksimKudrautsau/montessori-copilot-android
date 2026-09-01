package com.montessoricopilot.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.montessoricopilot.app.data.content.Areas
import com.montessoricopilot.app.ui.theme.AreaArtAndMusic
import com.montessoricopilot.app.ui.theme.AreaCultureAndNature
import com.montessoricopilot.app.ui.theme.AreaGraceAndCourtesy
import com.montessoricopilot.app.ui.theme.AreaLanguage
import com.montessoricopilot.app.ui.theme.AreaMathematics
import com.montessoricopilot.app.ui.theme.AreaMovement
import com.montessoricopilot.app.ui.theme.AreaPracticalLife
import com.montessoricopilot.app.ui.theme.AreaSensorial

/**
 * The visual identity of each curriculum area: one colour and one icon.
 *
 * These carry the app's visual weight until real photographs exist. Every
 * activity therefore *always* has something to show, and an activity that
 * later gains a photo simply replaces its tile — no empty grey boxes, and no
 * layout shift when images land.
 */

fun areaColor(area: String): Color = when (area) {
    Areas.PRACTICAL_LIFE -> AreaPracticalLife
    Areas.SENSORIAL -> AreaSensorial
    Areas.LANGUAGE -> AreaLanguage
    Areas.MATHEMATICS -> AreaMathematics
    Areas.MOVEMENT -> AreaMovement
    Areas.ART_AND_MUSIC -> AreaArtAndMusic
    Areas.GRACE_AND_COURTESY -> AreaGraceAndCourtesy
    Areas.CULTURE_AND_NATURE -> AreaCultureAndNature
    else -> AreaPracticalLife
}

fun areaIcon(area: String): ImageVector = when (area) {
    Areas.PRACTICAL_LIFE -> Icons.Filled.WaterDrop
    Areas.SENSORIAL -> Icons.Filled.TouchApp
    Areas.LANGUAGE -> Icons.Filled.RecordVoiceOver
    Areas.MATHEMATICS -> Icons.Filled.Calculate
    Areas.MOVEMENT -> Icons.Filled.DirectionsWalk
    Areas.ART_AND_MUSIC -> Icons.Filled.Brush
    Areas.GRACE_AND_COURTESY -> Icons.Filled.Groups
    Areas.CULTURE_AND_NATURE -> Icons.Filled.Park
    else -> Icons.Filled.WaterDrop
}
