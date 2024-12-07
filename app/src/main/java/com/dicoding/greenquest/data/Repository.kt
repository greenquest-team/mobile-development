package com.dicoding.greenquest.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.data.local.room.QuestDao
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.data.prefs.UserPreference
import com.dicoding.greenquest.data.remote.response.LoginResponse
import com.dicoding.greenquest.data.remote.response.PayloadItem
import com.dicoding.greenquest.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val apiServiceDummy: ApiService,
    private val questDao: QuestDao
){

    private var token: String? = null

    fun updateToken(newToken: String) {
        token = newToken
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiServiceDummy.login(email, password)
    }

    fun getAllStory(): LiveData<Result<List<QuestEntity>>> = liveData {
        emit(Result.Loading)

        try {
            val response = apiServiceDummy.getStories()
            val stories = response.listStory
            val storyList = stories.map { story ->
                QuestEntity(
                    story.id,
                    story.photoUrl,
                    story.name,
                    story.description,
                    story.createdAt
                )
            }
            questDao.deleteAllHealthNews()
            storyList.let { questDao.insert(it) }
        } catch (e: Exception) {
            Log.d("Repository", "getAllStory: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<QuestEntity>>> = questDao.getAllStory().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getWasteTypes(query: String): LiveData<Result<PayloadItem>> = liveData {
        emit(Result.Loading)

        try {
            // Panggil API untuk mendapatkan data
            val response = apiService.getWasteTypes(query)
            val wasteTypes = response.payload

            Log.d("TEST", "try jalan")

            if (wasteTypes.isEmpty()) {
                emit(Result.Error("No waste types found for query: $query"))
            } else {
                // Ambil satu data secara random
                val randomItem = wasteTypes.random()
                emit(Result.Success(randomItem))
            }


        } catch (e: Exception) {
            Log.e("WasteRepository", "getWasteTypesDirectly: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            apiServiceDummy: ApiService,
            questDao: QuestDao
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService, apiServiceDummy, questDao)
            }.also { instance = it }
    }
}