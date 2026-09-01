package com.montessoricopilot.app.data.content

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ActivityEntity::class,
        ActivityTextEntity::class,
        SensitivePeriodEntity::class,
        SensitivePeriodTextEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class ContentDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    companion object {
        @Volatile
        private var instance: ContentDatabase? = null

        /**
         * Builds (or returns) the content database, seeding it synchronously
         * from assets/content_seed.json the first time it is created.
         *
         * **Destructive migration is correct here.** This database is derived
         * data — every row comes from a file bundled in the APK — so on a
         * schema change the right move is to drop it and re-seed from the new
         * file. Nothing a parent typed lives here; that is all in userdata.db,
         * which never uses destructive migration.
         */
        fun build(context: Context): ContentDatabase {
            instance?.let { return it }
            return synchronized(this) {
                instance ?: run {
                    val appContext = context.applicationContext
                    Room.databaseBuilder(appContext, ContentDatabase::class.java, "content.db")
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                // Synchronous, inside Room's own creation
                                // transaction — no query can observe an empty
                                // library. See ContentSeed.kt.
                                seedContentDatabase(appContext, db)
                            }
                        })
                        // No-arg form: the `dropAllTables` parameter only
                        // exists in Room 2.7+, and this project pins 2.6.1
                        // (gradle/libs.versions.toml).
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { instance = it }
                }
            }
        }
    }
}
