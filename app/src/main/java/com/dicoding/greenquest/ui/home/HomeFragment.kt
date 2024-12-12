package com.dicoding.greenquest.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.greenquest.ui.scan.ScanActivity
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.databinding.FragmentHomeBinding
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.ui.main.MainActivity
import com.dicoding.greenquest.ui.materi.MateriActivity
import com.dicoding.greenquest.ui.mission.MissionActivity
import com.dicoding.greenquest.ui.quiz.QuizActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var animatorSet: AnimatorSet? = null

    private var userID = 0
    private var points = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//         Aksi pada tombol "Lihat"
        binding.btnLihat.setOnClickListener {
            val intent = Intent(requireContext(), ScanActivity::class.java)
            startActivity(intent)
        }

        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.textUsername.text = "Hey ${user.name}"
            Glide.with(binding.root.context)
                .load(user.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.ivAvatar)
            userID = user.user_id
            points = user.points
        }

        binding.circularProgressBar.progressMax = 100f

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvQuest.layoutManager = layoutManager
        binding.btnLihat.setOnClickListener {
          startActivity(Intent(requireContext(), MissionActivity::class.java))
        }

        observeViewModel()

        return root
    }

    override fun onPause() {
        super.onPause()
        animatorSet?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        animatorSet?.cancel() // Batalkan animasi untuk mencegah error
        animatorSet = null // Bersihkan referensi untuk menghindari memory leak
    }

    private fun observeViewModel() {
        homeViewModel.getQuest().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("HomeFragment", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("HomeFragment", "Success: ${result.data}")

                    val data = result.data

                    val completedQuests = data.count { it.isCompleted }
                    val totalQuests = data.size
                    val progressPercentage = if (totalQuests > 0) {
                        (completedQuests * 100) / totalQuests
                    } else {
                        0
                    }

                    // Update progress bar dan teks
                    val progressBarAnimator = ObjectAnimator.ofFloat(
                        binding.circularProgressBar,
                        "progress",
                        binding.circularProgressBar.progress,
                        progressPercentage.toFloat()
                    ).apply {
                        duration = 1000 // 1 second
                        interpolator = AccelerateDecelerateInterpolator() // Smooth easing
                    }

                    val textAnimator = ValueAnimator.ofInt(
                        binding.progressText.text.toString().removeSuffix("%").toInt(),
                        progressPercentage
                    ).apply {
                        duration = 1000 // 1 second
                        addUpdateListener { animation ->
                            val progress = animation.animatedValue as Int
                            binding.progressText.text = "$progress%" // Update text dynamically
                        }
                    }

                    animatorSet?.cancel() // Cancel previous animation if any
                    animatorSet = AnimatorSet().apply {
                        playTogether(progressBarAnimator, textAnimator)
                        start() // Play both animations simultaneously
                    }


                    // Filter quest yang belum selesai
                    val incompleteQuests = data.filter { !it.isCompleted }

                    if (incompleteQuests.isEmpty()) {
                        // Jika semua quest selesai, tampilkan pesan
                        binding.rvQuest.visibility = View.GONE
                        binding.tvNoQuestMessage.apply {
                            visibility = View.VISIBLE
                            text = "Anda sudah menyelesaikan semua quest hari ini"
                        }
                    } else {
                        // Tampilkan RecyclerView jika ada quest yang belum selesai
                        binding.rvQuest.visibility = View.VISIBLE
                        binding.tvNoQuestMessage.visibility = View.GONE

                        val adapter = QuestAdapter { quest ->
                            when (quest.questType) {
                                "scan" -> {
                                    val intent = Intent(requireContext(), ScanActivity::class.java)
                                    intent.putExtra(ScanActivity.QUEST_EXTRA, quest)
                                    startActivity(intent)
                                }
                                "material" -> {
                                    val intent = Intent(requireContext(), MateriActivity::class.java)
                                    intent.putExtra(MateriActivity.QUEST_EXTRA, quest)
                                    startActivity(intent)
                                }
                                "quiz" -> {
                                    val intent = Intent(requireContext(), QuizActivity::class.java)
                                    startActivity(intent)
                                }
                                "reminder" -> {
                                    showAlert("Reminder", "Apakah Anda yakin sudah membuang sampah pada tempatnya?", quest)
                                }
                                else -> {
                                    showToast("Unknown quest type: ${quest.questType}")
                                }
                            }
                        }

                        adapter.submitList(incompleteQuests)
                        binding.rvQuest.adapter = adapter
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    Log.e("HomeFragment", "Error: ${result.error}")
                    if (result.error.contains("401")) {
                        showToast("Unauthorized, please re-login.")
                        homeViewModel.logout()
                    } else {
                        showToast(result.error)
                    }
                }
                else -> {
                    showLoading(false)
                    Log.e("HomeFragment", "Unknown")
                    showToast("Unknown")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updateUserPoints(userId: Int, points: Int, newPoints: Int) {
        homeViewModel.updateUserPoints(userId, points, newPoints)
    }

    private fun showAlert(title: String, message: String, quest: QuestEntity) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Ya") { _, _ ->
                // Tampilkan pesan keberhasilan
                showToast("Yeah, kamu sudah berhasil menjadi pahlawan penjaga lingkungan!")
                homeViewModel.updateQuest(quest)

                updateUserPoints(userID, points, quest.pointsAwarded)

                observeViewModel()
            }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}