package com.dicoding.greenquest.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.greenquest.data.local.entity.QuestEntity

@Database(entities = [QuestEntity::class], version = 1, exportSchema = false)
abstract class QuestDatabase : RoomDatabase() {

    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var instance: QuestDatabase? = null
        fun getInstance(context: Context): QuestDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    QuestDatabase::class.java, "Quest.db"
                ).build()
            }
    }
}