package com.dicoding.greenquest.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.greenquest.data.local.entity.QuestEntity

@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: List<QuestEntity>)

    @Query("SELECT * FROM Story ORDER BY createdAt DESC")
    fun getAllStory(): LiveData<List<QuestEntity>>

    @Query("DELETE FROM Story")
    suspend fun deleteAllHealthNews()
}