package com.dicoding.greenquest.ui.quiz

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.data.remote.response.ErrorResponse
import com.dicoding.greenquest.data.remote.retrofit.ApiConfig
import com.dicoding.greenquest.di.Injection
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

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

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getQuiz(typeName: String) = repository.getQuiz(typeName)

    fun postQuiz(typeName: String, firstAnswer: String, secondAnswer: String) = liveData {
        emit(Result.Loading)
        try {
            val dataUser = repository.postQuiz(typeName, firstAnswer, secondAnswer)
            Log.d("QuizViewModel", "Success: ${dataUser.message}")
            emit(Result.Success(dataUser))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("QuizViewModel", "HttpException: ${errorBody.message}")
            emit(Result.Error(errorBody.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.e("QuizViewModel", "Exception: ${e.message}")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }
}