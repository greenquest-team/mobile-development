package com.dicoding.greenquest.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.databinding.ActivityLoginBinding
import com.dicoding.greenquest.ui.main.MainActivity
import com.dicoding.greenquest.ui.register.RegisterActivity
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
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
            viewModel.saveSession(UserModel(randomInt,"test123",email, "","sample_token"))
            AlertDialog.Builder(this).apply {
                setTitle("Yeah!")
                setMessage("Anda berhasil login. Sudah tidak sabar untuk belajar ya?")
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

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}