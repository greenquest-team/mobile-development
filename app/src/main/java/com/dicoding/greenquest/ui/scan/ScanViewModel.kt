package com.dicoding.greenquest.ui.scan

import androidx.lifecycle.ViewModel
import com.dicoding.greenquest.data.Repository

class ScanViewModel(private val repository: Repository) : ViewModel() {

    fun randomWasteType(label: String) = repository.getWasteTypes(label)

}