package com.dicoding.greenquest.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.ui.leaderboard.LeaderboardAdapter
import com.dicoding.greenquest.ui.leaderboard.LeaderboardViewModel
import com.dicoding.greenquest.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)
    }
}