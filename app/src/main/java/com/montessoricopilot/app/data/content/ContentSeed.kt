package com.montessoricopilot.app.data.content

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONObject

private const val TAG = "ContentSeed"
private const val SEED_ASSET = "content_seed.json"
private const val EXPECTED_SCHEMA_VERSION = 2

/**
 * Seeds the content database from assets/content_seed.json.
 *
 * This runs **synchronously inside Room's onCreate callback**, on the same
 * connection and inside the same transaction that creates the tables. That is
 * deliberate and fixes a real bug in v0.2: seeding used to be launched as a
 * coroutine, so the very first query could return an empty library before the
 * insert finished, and the user saw an empty Library tab on first launch.
 *
 * Because it runs inside onCreate, it must not touch Room DAOs (the database
 * isn't finished opening yet) — hence raw [SupportSQLiteDatabase] inserts and
 * org.json rather than kotlinx.serialization.
 */
internal fun seedContentDatabase(context: Context, db: SupportSQLiteDatabase) {
    val started = System.currentTimeMillis()

    val json = context.assets.open(SEED_ASSET).bufferedReader().use { it.readText() }
    val root = JSONObject(json)

    val schemaVersion = root.optInt("schemaVersion", -1)
    require(schemaVersion == EXPECTED_SCHEMA_VERSION) {
        "content_seed.json is schema v$schemaVersion but this build expects " +
            "v$EXPECTED_SCHEMA_VERSION — regenerate it with tools/generate_content_seed.py"
    }

    val activities = root.getJSONArray("activities")
    for (i in 0 until activities.length()) {
        val a = activities.getJSONObject(i)
        db.insert("activities", CONFLICT_ABORT, ContentValues().apply {
            put("id", a.getInt("id"))
            put("ageMinMonths", a.getInt("ageMinMonths"))
            put("ageMaxMonths", a.getInt("ageMaxMonths"))
            put("area", a.getString("area"))
            put("infantFocus", a.optStringOrNull("infantFocus"))
            put("sessionMinutes", a.getInt("sessionMinutes"))
            put("messLevel", a.getString("messLevel"))
            put("provenance", a.getString("provenance"))
            put("imageAsset", a.optStringOrNull("imageAsset"))
            put("imageCredit", a.optStringOrNull("imageCredit"))
            put("imageLicence", a.optStringOrNull("imageLicence"))
        })
    }

    val activityTexts = root.getJSONArray("activityTexts")
    for (i in 0 until activityTexts.length()) {
        val t = activityTexts.getJSONObject(i)
        db.insert("activity_texts", CONFLICT_ABORT, ContentValues().apply {
            put("activityId", t.getInt("activityId"))
            put("locale", t.getString("locale"))
            put("title", t.getString("title"))
            put("summary", t.getString("summary"))
            put("whyItMatters", t.getString("whyItMatters"))
            put("howToPresent", t.getString("howToPresent"))
            put("whatToObserve", t.getString("whatToObserve"))
            put("commonMistakes", t.getString("commonMistakes"))
            put("materialsNeeded", t.getString("materialsNeeded"))
            put("homemadeAlternative", t.getString("homemadeAlternative"))
            put("supervisionNote", t.optStringOrNull("supervisionNote"))
        })
    }

    val periods = root.getJSONArray("sensitivePeriods")
    for (i in 0 until periods.length()) {
        val p = periods.getJSONObject(i)
        db.insert("sensitive_periods", CONFLICT_ABORT, ContentValues().apply {
            put("id", p.getInt("id"))
            put("ageMinMonths", p.getInt("ageMinMonths"))
            put("ageMaxMonths", p.getInt("ageMaxMonths"))
        })
    }

    val periodTexts = root.getJSONArray("sensitivePeriodTexts")
    for (i in 0 until periodTexts.length()) {
        val t = periodTexts.getJSONObject(i)
        db.insert("sensitive_period_texts", CONFLICT_ABORT, ContentValues().apply {
            put("periodId", t.getInt("periodId"))
            put("locale", t.getString("locale"))
            put("name", t.getString("name"))
            put("description", t.getString("description"))
            put("whatYoullNotice", t.getString("whatYoullNotice"))
            put("howToSupport", t.getString("howToSupport"))
        })
    }

    Log.i(
        TAG,
        "Seeded content v$schemaVersion: ${activities.length()} activities, " +
            "${activityTexts.length()} texts, ${periods.length()} periods " +
            "in ${System.currentTimeMillis() - started}ms",
    )
}

/** org.json turns SQL NULL into the string "null" if you use optString(); this
 *  returns a real null so nullable columns stay nullable. */
private fun JSONObject.optStringOrNull(key: String): String? =
    if (isNull(key)) null else optString(key).takeIf { it.isNotEmpty() }

/** android.database.SQLiteDatabase.CONFLICT_ABORT. Seeding a freshly created
 *  database should never hit a conflict — if it does, the seed file has
 *  duplicate ids and we want to fail loudly rather than silently drop rows. */
private const val CONFLICT_ABORT = 2
