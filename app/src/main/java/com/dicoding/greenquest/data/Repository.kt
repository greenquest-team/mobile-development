package com.dicoding.greenquest.data

import com.dicoding.greenquest.data.local.entity.UserEntity
import com.dicoding.greenquest.data.prefs.UserPreference
import kotlinx.coroutines.flow.Flow

class Repository private constructor( private val userPreference: UserPreference){

    suspend fun saveSession(user: UserEntity) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserEntity> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference)
            }.also { instance = it }
    }
}