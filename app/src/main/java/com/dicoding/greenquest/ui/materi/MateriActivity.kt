package com.dicoding.greenquest.ui.materi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.greenquest.databinding.ActivityMateriBinding

class MateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMateriBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}