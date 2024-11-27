package com.dicoding.greenquest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.greenquest.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)


        // Cek apakah ini pertama kali aplikasi dibuka
//        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
//        val isFirstRun = prefs.getBoolean("isFirstRun", true)

//        if (isFirstRun) {
//            // Jika pertama kali, tampilkan halaman welcome
//            startActivity(Intent(this, WelcomeActivity::class.java))
//            // Set isFirstRun ke false agar tidak ditampilkan lagi
//            prefs.edit().putBoolean("isFirstRun", false).apply()
//            finish() // Menutup MainActivity agar tidak kembali ke halaman utama
//        } else {
//            // Jika bukan pertama kali, tampilkan MainActivity
//            setContentView(R.layout.activity_main)
//        }
    }
}