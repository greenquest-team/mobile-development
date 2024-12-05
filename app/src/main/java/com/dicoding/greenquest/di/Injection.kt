package com.dicoding.greenquest.di

import android.content.Context
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.room.QuestDatabase
import com.dicoding.greenquest.data.prefs.UserPreference
import com.dicoding.greenquest.data.prefs.dataStore
import com.dicoding.greenquest.data.remote.ApiType
import com.dicoding.greenquest.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    private var repository: Repository? = null

    fun provideRepository(context: Context): Repository {
        if (repository == null) {
            val pref = UserPreference.getInstance(context.dataStore)
            val session = runBlocking { pref.getSession().first() }
            val apiServiceReal = ApiConfig.getApiService(ApiType.REAL)
            val apiServiceDummy = ApiConfig.getApiService(ApiType.DUMMY)
            val questDatabase = QuestDatabase.getInstance(context)
            val questDao = questDatabase.questDao()
            return Repository.getInstance(pref, apiServiceReal, apiServiceDummy, questDao)

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