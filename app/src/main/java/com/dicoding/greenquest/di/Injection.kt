package com.dicoding.greenquest.di

import android.content.Context
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.room.QuestDatabase
import com.dicoding.greenquest.data.prefs.UserPreference
import com.dicoding.greenquest.data.prefs.dataStore
import com.dicoding.greenquest.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    private var repository: Repository? = null

    fun provideRepository(context: Context): Repository {
        if (repository == null) {
            val pref = UserPreference.getInstance(context.dataStore)
            val session = runBlocking { pref.getSession().first() }
            val apiServiceReal = ApiConfig.getApiService()
            val questDatabase = QuestDatabase.getInstance(context)
            val questDao = questDatabase.questDao()
            val leaderboardDao = questDatabase.leaderboardDao()
            repository =  Repository.getInstance(pref, apiServiceReal, questDao, leaderboardDao)

            session.token.let { token ->
                ApiConfig.setToken(token)
                repository?.updateToken(token)
            }
        }

        return repository!!
    }

    fun updateRepositoryToken(token: String) {
        repository?.updateToken(token)
    }
}