package com.dicoding.greenquest.data.prefs

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.user_id
            preferences[USER] = user.name
            preferences[USERNAME] = user.username
            preferences[EMAIL_KEY] = user.email
            preferences[PASSWORD] = user.password
            preferences[TOKEN_KEY] = user.token
            preferences[IMAGE] = user.image
            preferences[TGL] = user.tgl
            preferences[POINTS] = user.points
            preferences[IS_LOGIN_KEY] = true
            Log.d("UserPreference", "Session saved successfully")
        }
    }

    suspend fun savePoints(points: Int) {
        dataStore.edit { preferences ->
            preferences[POINTS] = points
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[ID_KEY] ?: 0,
                preferences[USER] ?: "",
                preferences[USERNAME] ?: "",
                preferences[EMAIL_KEY] ?:"",
                preferences[PASSWORD] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IMAGE] ?: "",
                preferences[TGL] ?: "",
                preferences[POINTS] ?: 0,
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val ID_KEY = intPreferencesKey("id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val USER = stringPreferencesKey("user")
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IMAGE = stringPreferencesKey("image")
        private val TGL = stringPreferencesKey("tgl")
        private val POINTS = intPreferencesKey("points")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}