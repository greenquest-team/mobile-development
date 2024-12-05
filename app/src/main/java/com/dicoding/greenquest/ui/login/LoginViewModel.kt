package com.dicoding.greenquest.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.prefs.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.remote.response.ErrorResponse
import com.dicoding.greenquest.data.remote.retrofit.ApiConfig
import com.dicoding.greenquest.di.Injection

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun login(email: String, password: String) = liveData {
    emit(Result.Loading)
    try {
        val dataUser = repository.login(email, password)
        Log.d("LoginViewModel", "Success: ${dataUser.message}")
        dataUser.loginResult?.let { user ->
            saveSession(UserModel(
                user_id = user.userId.toString(),
                name = user.name.toString(),
                email = email,
                password = password,
                token = user.token.toString()
            ))
            ApiConfig.setToken(user.token.toString())
            Injection.updateRepositoryToken(user.token.toString())
        }

        emit(Result.Success(dataUser.message))
    } catch (e: HttpException) {
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
        Log.e("LoginViewModel", "HttpException: ${errorBody.message}")
        emit(Result.Error(errorBody.message ?: "Unknown error"))
    } catch (e: Exception) {
        Log.e("LoginViewModel", "Exception: ${e.message}")
        emit(Result.Error(e.message ?: "Unknown error"))
    }
}

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}