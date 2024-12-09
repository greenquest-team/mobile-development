package com.dicoding.greenquest.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import kotlinx.coroutines.launch

class LeaderboardViewModel(private val repository: Repository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    fun getAllLeaderboard() = repository.getLeaderboard()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}