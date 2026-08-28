package com.montessoricopilot.app.data.content

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class ContentSeedFile(
    val activities: List<ActivityEntity>,
    val sensitivePeriods: List<SensitivePeriodEntity>,
)

private const val TAG = "ContentSeed"
private const val SEED_ASSET = "content_seed.json"

/**
 * Reads assets/content_seed.json (authored by /tools/generate_content_seed.py)
 * and inserts every row into an already-created, empty content database.
 * Called exactly once, from [ContentDatabase]'s onCreate callback — see that
 * file for why a callback is used instead of Room's createFromAsset().
 */
internal suspend fun seedContentDatabase(context: Context, dao: ContentDao) {
    val json = context.assets.open(SEED_ASSET).bufferedReader().use { it.readText() }
    val seed = Json { ignoreUnknownKeys = true }.decodeFromString<ContentSeedFile>(json)
    dao.insertActivities(seed.activities)
    dao.insertSensitivePeriods(seed.sensitivePeriods)
    Log.i(TAG, "Seeded content.db: ${seed.activities.size} activities, ${seed.sensitivePeriods.size} sensitive periods")
}
