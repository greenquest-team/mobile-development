package com.dicoding.greenquest.ui.materi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.greenquest.data.Repository
import com.dicoding.greenquest.data.local.entity.QuestEntity
import kotlinx.coroutines.launch

class MateriViewModel(private val repository: Repository) : ViewModel()  {

    fun getMaterial(typeName: String) = repository.getMaterial(typeName)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun updateQuest(quest: QuestEntity) {
        viewModelScope.launch {
            repository.updateQuest(quest, true)
        }
    }

}