package com.dicoding.greenquest.ui.customizeprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
            var session: UserModel? = null
            getSession().observeForever(object : Observer<UserModel> {
                override fun onChanged(value: UserModel) {
                    session = value
                    getSession().removeObserver(this)
                }
            })

            val updatedUserResponse = repository.updateUser(
                userId, name, username, email, tglLahir, points, avatar, password
            )

            val token = session?.token

            Log.d("TOKEENNNN", "$token")

            Log.d("CustomizeViewModel", "Success: ${updatedUserResponse.message}")

            updatedUserResponse.payload.let { user ->
                saveSession(
                    UserModel(
                        user_id = user.id,
                        name = user.name,
                        username = user.username,
                        email = user.email,
                        password = user.password,
                        token = token!!,
                        image = user.avatar,
                        tgl = user.tglLahir,
                        points = user.points.toInt()
                    )
                )
            }


            emit(Result.Success(updatedUserResponse.message))
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
            try {
                repository.saveSession(user)
                Log.d("CustomizeProfileViewModel", "Session updated: $user")
            } catch (e: Exception) {
                Log.e("CustomizeProfileViewModel", "Failed to save session: ${e.message}")
            }
        }
    }


}