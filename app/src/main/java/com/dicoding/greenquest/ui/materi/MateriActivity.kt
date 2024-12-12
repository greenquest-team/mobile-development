package com.dicoding.greenquest.ui.materi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.databinding.ActivityMateriBinding
import com.dicoding.greenquest.ui.home.HomeFragment
import com.dicoding.greenquest.ui.home.HomeViewModel
import com.dicoding.greenquest.ui.leaderboard.LeaderboardAdapter
import com.dicoding.greenquest.ui.main.MainActivity

class MateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMateriBinding

    private val materiViewModel: MateriViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var quest: QuestEntity

    private var userID = 0
    private var points = 0

    companion object{
        const val QUEST_EXTRA = "quest_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quest = intent.getParcelableExtra(QUEST_EXTRA)!!

        materiViewModel.getSession().observe(this) { user ->
            userID = user.user_id
            points = user.points
        }

        binding.btnBack.setOnClickListener { finish() }

        observeViewModel(quest.typeName)

    }

    private fun observeViewModel(typeName: String) {
        materiViewModel.getMaterial(typeName).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("MateriActivity", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("MateriActivity", "Success: ${result.data}")

                    val dataList = result.data

                    if (dataList.isNotEmpty()) {
                        val firstItem = dataList[0]
                        binding.tvMateriDesc.text = firstItem.descriptionMat
                        Glide.with(binding.root.context)
                            .load(firstItem.image)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                            .error(R.drawable.ic_error)
                            .into(binding.ivMateri)

                        binding.btnMateriSelesaiMembaca.setOnClickListener {
                            showAlert("Selamat", "Yeah, kamu sudah berhasil menjadi pahlawan penjaga lingkungan!", quest)
                        }
                    }

                }
                is Result.Error -> {
                    showLoading(false)
                    Log.e("MateriActivity", "Error: ${result.error}")
                    if (result.error.contains("401")) {
                        showToast("Unauthorized, please re-login.")
                        materiViewModel.logout()
                    } else {
                        showToast(result.error)
                    }
                }
                else -> {
                    showLoading(false)
                    Log.e("MateriActivity", "Unknown")
                    showToast("Unknown")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAlert(title: String, message: String, quest: QuestEntity) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Ya") { _, _ ->
                materiViewModel.updateQuest(quest)
                updateUserPoints(userID, points, quest.pointsAwarded)
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun updateUserPoints(userId: Int, points: Int, newPoints: Int) {
        materiViewModel.updateUserPoints(userId, points, newPoints)
    }
}