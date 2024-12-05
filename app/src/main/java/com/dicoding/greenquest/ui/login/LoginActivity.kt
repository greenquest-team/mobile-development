package com.dicoding.greenquest.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.databinding.ActivityLoginBinding
import com.dicoding.greenquest.ui.main.MainActivity
import com.dicoding.greenquest.ui.register.RegisterActivity
import kotlin.random.Random
import com.dicoding.greenquest.data.Result

class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val randomInt = Random.nextInt(1, 100)

        binding.loginButton.setOnClickListener {
            val email = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            observeViewModel(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel(email: String, password: String) {
        loginViewModel.login(email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("LoginActivity", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    showAlert("Yeah!", "Anda telah berhasil login. Yuk ceritakan pengalaman Anda!")
                    Log.d("LoginActivity", "Success: ${result.data}")
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert("Error", result.error)
                    Log.e("LoginActivity", "Error: ${result.error}")
                }
                else -> {
                    showLoading(false)
                    showAlert("Error", "Unknown")
                    Log.e("LoginActivity", "Unknown")
                }
            }
        }
    }

    private fun showAlert(title: String, message: String) {

        if (title == "Error") {
            AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("OK", null)
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}