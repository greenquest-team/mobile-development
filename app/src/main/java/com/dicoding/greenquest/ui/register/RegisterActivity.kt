package com.dicoding.greenquest.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.greenquest.R
import com.dicoding.greenquest.databinding.ActivityLoginBinding
import com.dicoding.greenquest.databinding.ActivityRegisterBinding
import com.dicoding.greenquest.ui.login.LoginActivity
import android.app.DatePickerDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.greenquest.ViewModelFactory
import java.util.*
import com.dicoding.greenquest.data.Result

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val signupViewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etTanggalLahir.setOnClickListener {
            showDatePickerDialog()
        }

        // Listener untuk tombol daftar
        binding.btnDaftar.setOnClickListener {
            val nama = binding.etNama.text.toString()
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val tanggalLahir = binding.etTanggalLahir.text.toString()
            val password = binding.etPassword.text.toString()

            // Validasi dan proses registrasi
            if (nama.isEmpty() || username.isEmpty() || email.isEmpty() || tanggalLahir.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Semua kolom harus diisi!")
            } else {
                // Lakukan proses registrasi
                observeViewModel(nama, username, email, tanggalLahir, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format tanggal ke dd/MM/yyyy
            val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            binding.etTanggalLahir.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun observeViewModel(name: String, username: String, email: String, tglLahir: String, password: String) {
        signupViewModel.register(name, username, email, tglLahir, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("SignupActivity", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    showAlert("Yeah!", "Akun dengan ${binding.etUsername.text} sudah jadi nih. Yuk, login dan mulai petualanganmu bersama GreenQuest.")
                    Log.d("SignupActivity", "Success: ${result.data}")
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert("Error", result.error)
                    Log.e("SignupActivity", "Error: ${result.error}")
                }
                else -> {
                    showLoading(false)
                    showAlert("Error", "Unknown")
                    Log.e("SignupActivity", "Unknown")
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