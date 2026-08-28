package com.montessoricopilot.app.data.content

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [ActivityEntity::class, SensitivePeriodEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ContentDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    companion object {
        // Holds the instance being built so the onCreate callback below can
        // reach its DAO. Safe because onCreate only fires on first real
        // access to the database (a query, not build()), which always
        // happens after `instance` has been assigned. This is the same
        // pattern Google's own Room sample apps (e.g. Sunflower) use for
        // seeding a database from a callback.
        @Volatile
        private var instance: ContentDatabase? = null

        /**
         * Builds (or returns the already-built) content database, seeding it
         * from assets/content_seed.json the first time it's created.
         *
         * `applicationScope` should live as long as the process — pass the
         * CoroutineScope MontessoriApp holds, not a screen/ViewModel scope,
         * since seeding must complete even if the first screen that
         * triggered it is gone by the time it finishes.
         */
        fun build(context: Context, applicationScope: CoroutineScope): ContentDatabase {
            instance?.let { return it }
            synchronized(this) {
                instance?.let { return it }
                val appContext = context.applicationContext
                val built = Room.databaseBuilder(appContext, ContentDatabase::class.java, "content.db")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            applicationScope.launch {
                                seedContentDatabase(appContext, requireNotNull(instance).contentDao())
                            }
                        }
                    })
                    .build()
                instance = built
                return built
            }
        }
    }
}
