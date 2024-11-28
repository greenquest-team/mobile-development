package com.dicoding.greenquest.data

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.greenquest.ui.scan.ScanActivity
import com.dicoding.greenquest.data.local.entity.UserEntity
import com.dicoding.greenquest.data.prefs.UserPreference
import com.dicoding.greenquest.data.remote.response.PayloadItem
import com.dicoding.greenquest.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class Repository private constructor( private val userPreference: UserPreference, private val apiService: ApiService){

    suspend fun saveSession(user: UserEntity) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserEntity> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
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
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}