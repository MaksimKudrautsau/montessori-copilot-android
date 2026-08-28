package com.montessoricopilot.app.data.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChildEntity::class,
        JournalEntryEntity::class,
        ShelfItemEntity::class,
        DismissedRecommendationEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        /**
         * Created on-device on first launch, read/write, holds this
         * household's actual data. This is the one database Android Auto
         * Backup is configured to include (see res/xml/backup_rules.xml) —
         * it's the only thing that's actually irreplaceable if the app is
         * reinstalled or the phone is replaced.
         */
        fun build(context: Context): UserDatabase =
            Room.databaseBuilder(context, UserDatabase::class.java, "userdata.db")
                .build()
    }
}
