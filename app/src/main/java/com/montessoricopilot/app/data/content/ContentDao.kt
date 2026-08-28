package com.montessoricopilot.app.data.content

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContentDao {

    // --- Seeding (called once, from ContentDatabase's onCreate callback) ---

    @Insert
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Insert
    suspend fun insertSensitivePeriods(periods: List<SensitivePeriodEntity>)

    // --- Reads ------------------------------------------------------------

    @Query("SELECT * FROM activities ORDER BY title")
    suspend fun getAllActivities(): List<ActivityEntity>

    @Query(
        "SELECT * FROM activities " +
            "WHERE :ageMonths BETWEEN ageMinMonths AND ageMaxMonths " +
            "ORDER BY category, title"
    )
    suspend fun getActivitiesForAge(ageMonths: Int): List<ActivityEntity>

    @Query(
        "SELECT * FROM activities " +
            "WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' " +
            "ORDER BY title"
    )
    suspend fun searchActivities(query: String): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE id IN (:ids)")
    suspend fun getActivitiesByIds(ids: List<Int>): List<ActivityEntity>

    @Query("SELECT * FROM sensitive_periods ORDER BY ageMinMonths")
    suspend fun getAllSensitivePeriods(): List<SensitivePeriodEntity>

    @Query(
        "SELECT * FROM sensitive_periods " +
            "WHERE :ageMonths BETWEEN ageMinMonths AND ageMaxMonths " +
            "ORDER BY ageMinMonths"
    )
    suspend fun getActiveSensitivePeriods(ageMonths: Int): List<SensitivePeriodEntity>
}
