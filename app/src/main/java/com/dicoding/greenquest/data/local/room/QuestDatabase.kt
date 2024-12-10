package com.dicoding.greenquest.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dicoding.greenquest.data.local.dao.LeaderboardDao
import com.dicoding.greenquest.data.local.entity.LeaderboardEntity
import com.dicoding.greenquest.data.local.entity.QuestEntity

@Database(
    entities = [QuestEntity::class, LeaderboardEntity::class],
    version = 6,
    exportSchema = false
)
abstract class QuestDatabase : RoomDatabase() {

    abstract fun questDao(): QuestDao
    abstract fun leaderboardDao(): LeaderboardDao

    companion object {
        @Volatile
        private var instance: QuestDatabase? = null
        fun getInstance(context: Context): QuestDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    QuestDatabase::class.java, "GreenQuest.db"
                ).addMigrations(MIGRATION_1_2).fallbackToDestructiveMigration().build().also { instance = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Buat ulang tabel dengan struktur baru
                database.execSQL("CREATE TABLE IF NOT EXISTS `leaderboard_new` (" +
                        "`id` INTEGER NOT NULL, " +
                        "`username` TEXT, " +
                        "`points` TEXT, " +
                        "PRIMARY KEY(`id`))")

                // Pindahkan data dari tabel lama ke tabel baru
                database.execSQL("INSERT INTO leaderboard_new (id, username, points) " +
                        "SELECT id, username, points FROM leaderboard")

                // Hapus tabel lama
                database.execSQL("DROP TABLE leaderboard")

                // Ganti nama tabel baru menjadi tabel lama
                database.execSQL("ALTER TABLE leaderboard_new RENAME TO leaderboard")
            }
        }
    }
}