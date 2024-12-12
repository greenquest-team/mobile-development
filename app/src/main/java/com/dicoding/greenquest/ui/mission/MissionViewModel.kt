package com.dicoding.greenquest.ui.mission

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.data.prefs.UserModel
import kotlinx.coroutines.launch

class MissionViewModel(private val repository: Repository) : ViewModel() {

    fun getQuestCompleted() = repository.getQuestCompleted()

    fun updateQuest(quest: QuestEntity) {
        viewModelScope.launch {
            repository.updateQuest(quest, true)
        }
    }

    fun updateUserPoints(userId: Int, points: Int, newPoints: Int) {
        viewModelScope.launch {
            try {
                val response = repository.updateUserPoints(userId, points, newPoints)
                if (response.code == 200) {
                    val allPoints = response.payload.points
                    repository.savePoints(allPoints.toInt())
                } else {
                    Log.e("UpdatePoints", "Gagal update poin ke API: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("UpdatePoints", "Error saat update poin: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}