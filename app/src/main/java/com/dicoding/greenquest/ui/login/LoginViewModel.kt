package com.dicoding.greenquest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.entity.UserEntity
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {
    fun saveSession(user: UserEntity) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}