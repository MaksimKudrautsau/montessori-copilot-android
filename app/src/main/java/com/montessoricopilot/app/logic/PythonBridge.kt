package com.montessoricopilot.app.logic

import com.chaquo.python.Python
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * The only class in the app that touches Chaquopy directly. Everything else
 * talks to plain Kotlin types; this converts them to/from the JSON strings
 * that app/src/main/python/logic/bridge.py expects and returns.
 *
 * Python must already be started (MontessoriApp.onCreate does this) before
 * any of these functions are called.
 */
object PythonBridge {

    private val json = Json { ignoreUnknownKeys = true }

    private fun bridgeModule() = Python.getInstance().getModule("logic.bridge")

    fun recommend(request: RecommendRequest): List<RecommendedActivityResult> {
        val payload = json.encodeToString(request)
        val resultJson = bridgeModule().callAttr("recommend_json", payload).toString()
        return json.decodeFromString(resultJson)
    }

    fun rotationStatus(request: RotationRequest): List<ShelfRotationResult> {
        val payload = json.encodeToString(request)
        val resultJson = bridgeModule().callAttr("rotation_status_json", payload).toString()
        return json.decodeFromString(resultJson)
    }

    /** Null when the child has no eligible activities — Python returns JSON
     *  null, which decodes straight to a Kotlin null. */
    fun dailyFocus(request: DailyFocusRequest): RecommendedActivityResult? {
        val payload = json.encodeToString(request)
        val resultJson = bridgeModule().callAttr("daily_focus_json", payload).toString()
        return json.decodeFromString(resultJson)
    }

    fun upcomingChanges(request: UpcomingChangesRequest): UpcomingChangesResult {
        val payload = json.encodeToString(request)
        val resultJson = bridgeModule().callAttr("upcoming_changes_json", payload).toString()
        return json.decodeFromString(resultJson)
    }
}
