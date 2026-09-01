package com.montessoricopilot.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/** Per-item delay. Small enough to read as one movement rather than items
 *  arriving one at a time. */
private const val STAGGER_STEP_MS = 40

/** Beyond this many items the stagger is capped, so item 30 doesn't wait
 *  over a second to appear. */
private const val MAX_STAGGER_STEPS = 8

private const val ENTRANCE_DURATION_MS = 220

/**
 * Fades and lifts a list item into place, offset slightly by its position.
 *
 * Deliberately restrained: the PRD's motion rule is that nothing may delay a
 * tap, and that the app should feel calm. The content is laid out immediately
 * and only its appearance animates, so scrolling and tapping are never gated
 * on an animation finishing.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    content: @Composable () -> Unit,
) {
    // Only the first screenful animates. Beyond that an item starts visible,
    // for two reasons: scrolling back up shouldn't replay the animation every
    // time an item is recycled into view, and an item further down the list
    // shouldn't wait on a delay before it can be read.
    val animates = index < MAX_STAGGER_STEPS
    var visible by remember { mutableStateOf(!animates) }

    LaunchedEffect(Unit) {
        if (animates) {
            delay((index * STAGGER_STEP_MS).toLong())
            visible = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(ENTRANCE_DURATION_MS)) +
            slideInVertically(
                animationSpec = tween(ENTRANCE_DURATION_MS, easing = LinearOutSlowInEasing),
                // A short lift — about a third of a card height, not a slide
                // from off-screen.
                initialOffsetY = { it / 3 },
            ),
    ) {
        content()
    }
}
