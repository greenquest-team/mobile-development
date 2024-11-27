package com.dicoding.greenquest.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.entity.UserEntity
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun getSession(): LiveData<UserEntity> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}