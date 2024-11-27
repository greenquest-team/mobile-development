package com.dicoding.greenquest.di

import android.content.Context
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.prefs.UserPreference
import com.dicoding.greenquest.data.prefs.dataStore

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        return Repository.getInstance(pref)
    }
}