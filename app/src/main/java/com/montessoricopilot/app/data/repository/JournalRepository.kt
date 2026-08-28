package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.user.JournalEntryEntity
import com.montessoricopilot.app.data.user.UserDao
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class JournalRepository(private val userDao: UserDao) {

    fun observeForChild(childId: Int): Flow<List<JournalEntryEntity>> =
        userDao.observeJournalForChild(childId)

    suspend fun addEntry(childId: Int, category: String, note: String) {
        userDao.insertJournalEntry(
            JournalEntryEntity(
                childId = childId,
                timestampEpochMillis = Instant.now().toEpochMilli(),
                category = category,
                note = note,
            )
        )
    }

    suspend fun deleteEntry(entry: JournalEntryEntity) = userDao.deleteJournalEntry(entry)
}
