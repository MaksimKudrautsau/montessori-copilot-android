package com.montessoricopilot.app.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.montessoricopilot.app.ui.areaColor
import com.montessoricopilot.app.ui.areaIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The image for an activity, or — when it has none yet — a tinted tile carrying
 * that curriculum area's icon.
 *
 * The fallback is the normal case today: no photographs have been sourced or
 * licence-checked yet (PRD v0.5 §6.4). It is designed to look deliberate rather
 * than broken, and occupies exactly the space a real photo will, so adding
 * images later causes no layout shift.
 *
 * Images are read from the APK's assets by [imageAsset] name. No network and no
 * image-loading library: the app declares no INTERNET permission, and adding
 * Coil/Glide for local files would be weight for nothing.
 */
@Composable
fun ActivityImage(
    area: String,
    imageAsset: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    iconSize: Dp = 32.dp,
) {
    val context = LocalContext.current
    var bitmap by remember(imageAsset) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageAsset) {
        bitmap = imageAsset?.let { name ->
            // Decoding touches disk; keep it off the main thread. A missing or
            // corrupt asset falls back to the tile rather than crashing.
            withContext(Dispatchers.IO) {
                runCatching {
                    context.assets.open("images/$name").use {
                        BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    val tint = areaColor(area)

    Box(
        modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center,
    ) {
        val loaded = bitmap
        if (loaded != null) {
            Image(
                bitmap = loaded,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // A soft vertical wash rather than a flat block — reads as a
            // designed surface instead of a missing asset.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(tint.copy(alpha = 0.22f), tint.copy(alpha = 0.40f)),
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = areaIcon(area),
                    contentDescription = null,
                    tint = tint.copy(alpha = 0.85f),
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}

/** Small square variant used in list rows. */
@Composable
fun ActivityThumbnail(area: String, imageAsset: String?, modifier: Modifier = Modifier) {
    ActivityImage(
        area = area,
        imageAsset = imageAsset,
        modifier = modifier.size(64.dp),
        cornerRadius = 10.dp,
        iconSize = 26.dp,
    )
}
