package com.dicoding.greenquest.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.greenquest.data.local.entity.LeaderboardEntity
import com.dicoding.greenquest.data.local.entity.QuestEntity

@Dao
interface QuestDao {
    @Query("SELECT * FROM UserQuest WHERE createdAt BETWEEN :startOfDay AND :endOfDay")
    fun getQuestByDateRange(startOfDay: Long, endOfDay: Long): List<QuestEntity>

    @Query("SELECT * FROM UserQuest WHERE isCompleted = 1")
    fun getQuestIsCompleted(): LiveData<List<QuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quests: List<QuestEntity>)

    @Query("DELETE FROM UserQuest")
    suspend fun deleteAll()

    @Update
    suspend fun updateQuest(quests: QuestEntity)
}