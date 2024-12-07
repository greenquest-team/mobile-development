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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.greenquest.ui.scan.ScanActivity
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.prefs.UserModel
import com.dicoding.greenquest.databinding.FragmentHomeBinding
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.local.entity.QuestEntity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var animatorSet: AnimatorSet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Aksi pada tombol "Lihat"
//        binding.btnLihat.setOnClickListener {
//            val intent = Intent(requireContext(), ScanActivity::class.java)
//            startActivity(intent)
//        }

        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.textUsername.text = "Hey ${user.name}"
        }

        binding.circularProgressBar.progressMax = 100f

        val progressBarAnimator = ObjectAnimator.ofFloat(binding.circularProgressBar, "progress", 0f, 50f).apply {
            duration = 1000 // 1 second
            interpolator = AccelerateDecelerateInterpolator() // Smooth easing
        }

        val textAnimator = ValueAnimator.ofInt(0, 50).apply {
            duration = 1000 // 1 second
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                binding.progressText.text = "$progress%" // Update text dynamically
            }
        }

        // Combine both animations into an AnimatorSet
        animatorSet = AnimatorSet().apply {
            playTogether(progressBarAnimator, textAnimator)
            start()// Play both animations simultaneously
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvQuest.layoutManager = layoutManager


        observeViewModel()

//        val user:


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
        homeViewModel.getAllStory().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("HomeFragment", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("HomeFragment", "Success: ${result.data}")

                    val data = result.data
                    val adapter = QuestAdapter()
                    adapter.submitList(data)
                    binding.rvQuest.adapter = adapter

                    adapter.setOnItemClickCallback(object : QuestAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: QuestEntity) {
                            val detailActivityIntent = Intent(requireContext(), ScanActivity::class.java)
                            startActivity(detailActivityIntent)
                        }
                    })
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}