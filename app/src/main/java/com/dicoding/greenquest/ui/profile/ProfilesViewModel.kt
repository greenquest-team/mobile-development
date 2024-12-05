package com.dicoding.greenquest.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import kotlinx.coroutines.launch

class ProfilesViewModel(private val repository: Repository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}