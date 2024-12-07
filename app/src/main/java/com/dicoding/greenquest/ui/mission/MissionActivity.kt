package com.dicoding.greenquest.ui.mission

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.greenquest.R
import com.dicoding.greenquest.databinding.ActivityMissionBinding
import com.dicoding.greenquest.databinding.ActivityScanBinding

class MissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}