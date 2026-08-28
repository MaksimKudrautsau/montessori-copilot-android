package com.montessoricopilot.app.data.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    /** java.time.LocalDate.toEpochDay() — timezone-free, DST-safe. */
    val birthDateEpochDay: Long,
)

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = ChildEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val timestampEpochMillis: Long,
    val category: String, // motor / language / practical_life / behavior / other
    val note: String,
)

@Entity(
    tableName = "shelf_items",
    foreignKeys = [
        ForeignKey(
            entity = ChildEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class ShelfItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    /** Nullable: links back to content.db's activities.id when the item came
     *  from the curated library. No DB-level foreign key across the two
     *  databases is possible (they're separate files) — resolved by the
     *  repository layer instead. Null for a parent's own custom item. */
    val activityId: Int?,
    val customTitle: String?,
    val status: String, // "active" | "storage"
    val datePlacedEpochDay: Long,
)

@Entity(
    tableName = "dismissed_recommendations",
    foreignKeys = [
        ForeignKey(
            entity = ChildEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class DismissedRecommendationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val activityId: Int,
    val dismissedAtEpochMillis: Long,
)
