package com.dicoding.greenquest.ui.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.entity.QuestEntity
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: Repository) : ViewModel() {
    fun updateUserPoints(userId: Int, points: Int, newPoints: Int) {
        viewModelScope.launch {
            try {
                val response = repository.updateUserPoints(userId, points, newPoints)
                if (response.code == 200) {
                    val allPoints = points + newPoints
                    repository.savePoints(allPoints) // Panggil UserPreference
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

    fun updateQuest(quest: QuestEntity) {
        viewModelScope.launch {
            repository.updateQuest(quest, true)
        }
    }
}