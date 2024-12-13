package com.dicoding.greenquest.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.greenquest.data.local.dao.LeaderboardDao
import com.dicoding.greenquest.data.local.entity.LeaderboardEntity
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.data.local.room.QuestDao
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.data.prefs.UserPreference
import com.dicoding.greenquest.data.remote.response.LoginResponse
import com.dicoding.greenquest.data.remote.response.MaterialPayloadItem
import com.dicoding.greenquest.data.remote.response.QuizPayloadItem
import com.dicoding.greenquest.data.remote.response.QuizPostResponse
import com.dicoding.greenquest.data.remote.response.RegisterResponse
import com.dicoding.greenquest.data.remote.response.UserPayload
import com.dicoding.greenquest.data.remote.response.UserResponse
import com.dicoding.greenquest.data.remote.response.WasteTypePayloadItem
import com.dicoding.greenquest.data.remote.retrofit.ApiService
import com.dicoding.greenquest.getEndOfLocalDay
import com.dicoding.greenquest.getStartOfLocalDay
import com.dicoding.greenquest.mapQuestResponseToEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val questDao: QuestDao,
    private val leaderboardDao: LeaderboardDao
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

    suspend fun savePoints(points: Int) {
        userPreference.savePoints(points)
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(username: String, password: String): LoginResponse {
        return apiService.login(username, password)
    }

    suspend fun register(name: String, username: String, email: String, tglLahir: String, password: String): RegisterResponse {
        return apiService.register(name, username, email, tglLahir, password)
    }

    suspend fun updateUser(
        userId: Int,
        name: String,
        username: String,
        email: String,
        tglLahir: String,
        points: Int,
        avatar: String,
        password: String
    ): UserResponse {
        // Buat payload
        val payload = UserPayload(
            id = userId,
            name = name,
            username = username,
            email = email,
            tglLahir = tglLahir,
            points = points.toString(),
            avatar = avatar,
            password = password
        )
        return apiService.updateUser(userId, payload)
    }

    suspend fun updateUserPoints(userId: Int, points: Int, newPoints: Int): UserResponse {
        val totalPoints = points + newPoints
        return apiService.updateUserPoints(userId, totalPoints.toString())
    }

    fun getQuest(): LiveData<Result<List<QuestEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        val startOfDay = getStartOfLocalDay()
        val endOfDay = getEndOfLocalDay()

        val entity: List<QuestEntity>

        try {
            // Ambil userId dari DataStore
            val userId = userPreference.getSession().map { it.user_id }.first()

            // Periksa data lokal berdasarkan tanggal hari ini
            val quests =
            questDao.getQuestByDateRange(startOfDay, endOfDay)
            if (quests.isNotEmpty()) {
                // Jika data lokal untuk hari ini tersedia, kembalikan data tersebut
                emit(Result.Success(quests))
            } else {
                // Jika tidak ada data lokal untuk hari ini, ambil dari API
                val response = apiService.getQuest()
                val questScanList = response.payload?.questScan
                val questReminderList = response.payload?.questReminder
                val questQuizList = response.payload?.questQuiz
                val questMaterialList = response.payload?.questMaterial

                // Konversi data API menjadi entitas
                val scanEntities = mapQuestResponseToEntity(questScanList, userId)
                val reminderEntities = mapQuestResponseToEntity(questReminderList, userId)
                val quizEntities = mapQuestResponseToEntity(questQuizList, userId)
                val materialEntities = mapQuestResponseToEntity(questMaterialList, userId)

                val combinedEntities = (scanEntities + materialEntities + reminderEntities + quizEntities).map { entity ->
                    entity.copy(createdAt = System.currentTimeMillis()) // Tambahkan timestamp hari ini
                }

                questDao.insertAll(combinedEntities)

                entity = questDao.getQuestByDateRange(startOfDay, endOfDay)

                emit(Result.Success(entity))
            }
        } catch (e: Exception) {
            Log.e("Repository", "getQuest: ${e.message}", e)
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getQuestCompleted(): LiveData<List<QuestEntity>> {
        return questDao.getQuestIsCompleted()
    }

    suspend fun updateQuest(quest: QuestEntity, completedState: Boolean) {
        val existingQuest = questDao.getQuestById(quest.id)
        if (existingQuest != null) {
            quest.isCompleted = completedState
            questDao.updateQuest(quest)
            Log.d("Repository", "Quest with ID ${quest.id} found in database.")
        } else {
            Log.e("Repository", "Quest with ID ${quest.id} not found in database.")
        }
    }

    fun getLeaderboard(): LiveData<Result<List<LeaderboardEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try {
            val response = apiService.getLeaderboard()
            val leaderboards = response.payload
            val leaderboardList = leaderboards.map { leaderboard ->
                LeaderboardEntity(
                    username = leaderboard.username,
                    points = leaderboard.points,
                    avatar = leaderboard.avatar.toString(),
                )
            }
            leaderboardDao.deleteAllLeadeboardData()
            leaderboardList.let { leaderboardDao.insertLeaderboard(it) }
        } catch (e: Exception) {
            Log.d("Repository", "getAllStory: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<LeaderboardEntity>>> = leaderboardDao.getAllLeaderboard().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getMaterial(query: String): LiveData<Result<List<MaterialPayloadItem>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try {
            // Panggil API untuk mendapatkan data
            val response = apiService.getMaterial(query)
            val material = response.payload

            if (material.isEmpty()) {
                emit(Result.Error("No material found for query: $query"))
            } else {
                emit(Result.Success(material))
            }

        } catch (e: Exception) {
            Log.e("Repository", "getMaterials: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getQuiz(query: String): LiveData<Result<List<QuizPayloadItem>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try {
            // Panggil API untuk mendapatkan data
            val response = apiService.getQuiz(query)
            val quizzez = response.payload

            emit(Result.Success(quizzez))

        } catch (e: Exception) {
            Log.e("Repository", "getQuiz: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun postQuiz(typeName: String, firstAnswer: String, secondAnswer: String): QuizPostResponse {
        return apiService.postQuiz(typeName, firstAnswer, secondAnswer)
    }

    fun getWasteTypes(query: String): LiveData<Result<WasteTypePayloadItem>> = liveData(Dispatchers.IO) {
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
            questDao: QuestDao,
            leaderboardDao: LeaderboardDao
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService, questDao, leaderboardDao)
            }.also { instance = it }
    }
}