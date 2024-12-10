package com.dicoding.greenquest.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.greenquest.R
import com.dicoding.greenquest.databinding.ActivityCustomizeProfileBinding

class CustomizeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomizeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}