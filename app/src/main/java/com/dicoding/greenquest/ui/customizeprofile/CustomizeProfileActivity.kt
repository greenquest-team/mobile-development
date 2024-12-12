package com.dicoding.greenquest.ui.customizeprofile

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.databinding.ActivityCustomizeProfileBinding
import com.dicoding.greenquest.ui.home.HomeViewModel
import com.dicoding.greenquest.ui.main.MainActivity
import java.util.Calendar

class CustomizeProfileActivity : AppCompatActivity() {

    private val customizeViewModel: CustomizeProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityCustomizeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etTgl.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnBack.setOnClickListener { finish() }

        customizeViewModel.getSession().observe(this) { user ->
            if (user != null) {
                Log.d("CustomizeProfileActivity", "Updated user: $user")
                binding.etName.setText(user.name)
                binding.etUsername.setText(user.username)
                binding.etEmail.setText(user.email)
                binding.etTgl.setText(user.tgl)
                Glide.with(binding.root.context)
                    .load(user.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                    .error(R.drawable.ic_error)
                    .into(binding.ivAvatar)

                binding.buttonSimpan.setOnClickListener {
                    val userId = user.user_id
                    val name = binding.etName.text
                    val username = user.username
                    val email = user.email
                    val tgl = binding.etTgl.text
                    val points = user.points
                    val image = user.image
                    val password = if (binding.etPassword.text.isNullOrEmpty()) user.password else binding.etPassword.text.toString()

                    observeViewModeL(userId, name.toString(), username, email, tgl.toString(), points.toInt(), image, password)
                }
            } else {
                showAlert("Error", "User tidak ditemukan. Silakan coba login kembali.")
            }
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
            binding.etTgl.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun observeViewModeL(userId: Int, name: String, username: String, email: String, tglLahir: String, points: Int, avatar: String, password: String) {
        if (customizeViewModel.isValidUserData(name, username, email, tglLahir)) {
            customizeViewModel.updateUser(userId, name, username, email, tglLahir, points, avatar, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                        Log.d("CustomizeActivity", "Loading...")
                    }
                    is Result.Success -> {
                        showLoading(false)
                        showAlert("Yeah!", "Anda telah berhasil Update!")
                        Log.d("CustomizeActivity", "Success: ${result.data}")

                    }
                    is Result.Error -> {
                        showLoading(false)
                        showAlert("Error", result.error)
                        Log.e("CustomizeActivity", "Error: ${result.error}")
                    }
                    else -> {
                        showLoading(false)
                        showAlert("Error", "Unknown")
                        Log.e("CustomizeActivity", "Unknown")
                    }
                }
            }
        } else {
            showAlert("Error", "Masih ada bagian yang belum terusu")
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