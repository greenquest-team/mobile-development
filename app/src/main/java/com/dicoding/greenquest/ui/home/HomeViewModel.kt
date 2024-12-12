package com.dicoding.greenquest.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.data.prefs.UserModel
import kotlinx.coroutines.launch
import com.dicoding.greenquest.data.Result
import retrofit2.HttpException

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Click to Scan"
    }
    val text: LiveData<String> = _text

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getQuest() = repository.getQuest()

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
}