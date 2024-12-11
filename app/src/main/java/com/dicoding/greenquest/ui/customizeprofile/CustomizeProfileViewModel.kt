package com.dicoding.greenquest.ui.customizeprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.data.remote.response.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.lifecycle.liveData as liveData

class CustomizeProfileViewModel(private val repository: Repository) : ViewModel() {

    fun updateUser(
        userId: Int,
        name: String,
        username: String,
        email: String,
        tglLahir: String,
        points: Int,
        avatar: String,
        password: String
    ) = liveData {
        emit(Result.Loading)
        try {
            // Memanggil repository untuk mengupdate data user
            val updatedUserResponse = repository.updateUser(
                userId, name, username, email, tglLahir, points, avatar, password
            )

            // Jika berhasil, simpan user terbaru ke datastore
            val updatedUser = updatedUserResponse.payload.user
            val curentsession = getSession().value
            val token = curentsession?.token
            saveSession(
                UserModel(
                    user_id = updatedUser.id,
                    name = updatedUser.name,
                    username = updatedUser.username,
                    email = updatedUser.email,
                    password = password,
                    token = token!!, // Sesuaikan jika ada token
                    image = updatedUser.avatar,
                    points = updatedUser.points
                )
            )

            emit(Result.Success(updatedUser))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody ?: "Unknown error occurred"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun isValidUserData(name: String, username: String, email: String, tglLahir: String): Boolean {
        return name.isNotBlank() &&
                username.isNotBlank() &&
                email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                tglLahir.isNotBlank()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }


}