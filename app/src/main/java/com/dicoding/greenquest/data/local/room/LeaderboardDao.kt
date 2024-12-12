package com.dicoding.greenquest.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dicoding.greenquest.data.local.entity.LeaderboardEntity

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard ORDER BY CAST(points AS INTEGER) DESC")
    fun getAllLeaderboard(): LiveData<List<LeaderboardEntity>>

    @Insert
    fun insertLeaderboard(leaderboard: List<LeaderboardEntity>)

    @Update
    fun updateLeaderboard(leaderboard: LeaderboardEntity)

    @Query("DELETE FROM leaderboard")
    suspend fun deleteAllLeadeboardData()
}
