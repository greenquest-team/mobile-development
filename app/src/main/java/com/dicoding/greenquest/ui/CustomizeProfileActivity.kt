package com.dicoding.greenquest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.greenquest.databinding.ActivityCustomizeProfileBinding

class CustomizeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomizeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish() // Menutup activity dan kembali ke fragment sebelumnya
        }
    }
}