package com.montessoricopilot.app.ui.theme

import androidx.compose.ui.graphics.Color

// Core palette — warm, natural, wood/earth tones rather than a stock Material
// blue, to match a Montessori "prepared environment" feel.
val Terracotta = Color(0xFFB5654B)
val Sage = Color(0xFF6E8266)
val Cream = Color(0xFFFBF4EA)
val WalnutBrown = Color(0xFF4A3B31)
val SandBeige = Color(0xFFE8DCC8)
val SoftClay = Color(0xFFD98E73)
val DarkSurface = Color(0xFF3A2E26)

// --- Curriculum area accents ------------------------------------------------
// One muted, desaturated accent per area, all sharing roughly the same
// lightness and saturation so no single area shouts. These are used for the
// activity tile behind each area's icon — the app's main source of colour
// until real photographs land. Deliberately kept low-chroma: this is a calm
// app, and eight loud colours in one list would undo that.

val AreaPracticalLife = Color(0xFFC08552)   // warm ochre — water, wood, work
val AreaSensorial = Color(0xFF9C6B98)       // soft plum — the classic pink tower
val AreaLanguage = Color(0xFF4F7CAC)        // muted blue — letters, speech
val AreaMathematics = Color(0xFFB5654B)     // terracotta — red/blue number rods
val AreaMovement = Color(0xFF6E8266)        // sage — floor, outdoors, walking
val AreaArtAndMusic = Color(0xFFC77B4E)     // burnt orange — pigment, warmth
val AreaGraceAndCourtesy = Color(0xFF7E8B99) // slate — social, calm, neutral
val AreaCultureAndNature = Color(0xFF6D8C6A) // leaf green — plants, seasons
