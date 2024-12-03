package com.dicoding.greenquest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Click to Scan"
    }
    val text: LiveData<String> = _text

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}