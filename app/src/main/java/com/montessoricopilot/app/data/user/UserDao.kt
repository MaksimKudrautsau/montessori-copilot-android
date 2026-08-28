package com.montessoricopilot.app.data.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // --- Children -----------------------------------------------------

    @Query("SELECT * FROM children ORDER BY name")
    fun observeChildren(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE id = :childId")
    suspend fun getChild(childId: Int): ChildEntity?

    @Insert
    suspend fun insertChild(child: ChildEntity): Long

    @Update
    suspend fun updateChild(child: ChildEntity)

    @Delete
    suspend fun deleteChild(child: ChildEntity)

    // --- Journal --------------------------------------------------------

    @Query("SELECT * FROM journal_entries WHERE childId = :childId ORDER BY timestampEpochMillis DESC")
    fun observeJournalForChild(childId: Int): Flow<List<JournalEntryEntity>>

    @Insert
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntryEntity)

    // --- Shelf ------------------------------------------------------------

    @Query("SELECT * FROM shelf_items WHERE childId = :childId ORDER BY status, datePlacedEpochDay")
    fun observeShelfForChild(childId: Int): Flow<List<ShelfItemEntity>>

    @Insert
    suspend fun insertShelfItem(item: ShelfItemEntity): Long

    @Update
    suspend fun updateShelfItem(item: ShelfItemEntity)

    @Delete
    suspend fun deleteShelfItem(item: ShelfItemEntity)

    // --- Dismissed recommendations ----------------------------------------

    @Query("SELECT activityId FROM dismissed_recommendations WHERE childId = :childId")
    suspend fun getDismissedActivityIds(childId: Int): List<Int>

    @Insert
    suspend fun insertDismissed(dismissed: DismissedRecommendationEntity): Long
}
