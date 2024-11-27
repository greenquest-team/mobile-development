package com.dicoding.greenquest.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ScanActivity
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.ActivityMainBinding
import com.dicoding.greenquest.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                // Jika belum login, langsung arahkan ke LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                // Jika sudah login, baru tampilkan layout
                setupView()
            }
        }
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_leaderboard, R.id.navigation_profiles
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val bottomNav = binding.navView
//        bottomNav. = ContextCompat.getColorStateList(this, R.color.white)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}