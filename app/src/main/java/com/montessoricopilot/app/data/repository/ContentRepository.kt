package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.content.ActivityEntity
import com.montessoricopilot.app.data.content.ContentDao
import com.montessoricopilot.app.data.content.SensitivePeriodEntity

class ContentRepository(private val contentDao: ContentDao) {

    suspend fun allActivities(): List<ActivityEntity> = contentDao.getAllActivities()

    suspend fun activitiesForAge(ageMonths: Int): List<ActivityEntity> =
        contentDao.getActivitiesForAge(ageMonths)

    suspend fun search(query: String): List<ActivityEntity> = contentDao.searchActivities(query)

    suspend fun activitiesByIds(ids: List<Int>): List<ActivityEntity> =
        if (ids.isEmpty()) emptyList() else contentDao.getActivitiesByIds(ids)

    suspend fun activeSensitivePeriods(ageMonths: Int): List<SensitivePeriodEntity> =
        contentDao.getActiveSensitivePeriods(ageMonths)
}
