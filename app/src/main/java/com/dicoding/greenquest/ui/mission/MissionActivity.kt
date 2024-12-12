package com.dicoding.greenquest.ui.mission

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.ActivityMissionBinding

class MissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMissionBinding

    private val missionViewModel: MissionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMisi.setOnClickListener { finish() }

        val layoutManagerBerlangsung = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvMisi.layoutManager = layoutManagerBerlangsung

        missionViewModel.getQuestCompleted().observe(this) { questList ->
            showLoading(true)
            if (questList != null && questList.isNotEmpty()) {
                showLoading(false)
                val adapter = RiwayatMissionAdapter()
                adapter.submitList(questList)
                binding.rvMisi.adapter = adapter
            } else {
                showToast("no history found")
                binding.rvMisi.visibility = View.INVISIBLE
                binding.tvNoQuestMessage.apply {
                    visibility = View.VISIBLE
                    text = "Tidak ada quest yang kamu selesaikan sejauh ini"
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
}