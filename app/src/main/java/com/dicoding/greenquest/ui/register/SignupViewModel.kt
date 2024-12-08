package com.dicoding.greenquest.ui.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.greenquest.data.Repository
import com.google.gson.Gson
import retrofit2.HttpException
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.remote.response.ErrorResponse

class SignupViewModel(private val repository: Repository) : ViewModel() {

    fun register(name: String, username: String, email: String, tglLahir: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val dataUser = repository.register(name, username, email, tglLahir, password)
            Log.d("SignupViewModel", "Success: ${dataUser.message}")
            emit(Result.Success(dataUser.message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("SignupViewModel", "HttpException: ${errorBody.message}")
            emit(Result.Error(errorBody.message ?: "Unknown error"))
        } catch (e: Exception) {
            Log.e("SignupViewModel", "Exception: ${e.message}")
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }
}