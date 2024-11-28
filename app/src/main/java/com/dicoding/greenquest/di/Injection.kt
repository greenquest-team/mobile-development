package com.dicoding.greenquest.di

import android.content.Context
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.prefs.UserPreference
import com.dicoding.greenquest.data.prefs.dataStore
import com.dicoding.greenquest.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(pref, apiService)
    }
}