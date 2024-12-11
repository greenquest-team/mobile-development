package com.dicoding.greenquest.ui.mission

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.greenquest.R
import com.dicoding.greenquest.databinding.ActivityMissionBinding
import com.dicoding.greenquest.databinding.ActivityScanBinding
import com.dicoding.greenquest.ui.home.HomeFragment
import com.dicoding.greenquest.ui.main.MainActivity

class MissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMisi.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}