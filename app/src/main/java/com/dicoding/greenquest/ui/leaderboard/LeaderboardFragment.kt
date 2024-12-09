package com.dicoding.greenquest.ui.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.FragmentLeaderboardBinding
import com.dicoding.greenquest.data.Result

class LeaderboardFragment : Fragment() {

    private val leaderboardViewModel: LeaderboardViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentLeaderboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Contoh penggunaan binding untuk View di fragment_leaderboard.xml
        binding.titleLeaderboard.text = "Leaderboard"
        // ... binding untuk View lainnya ...

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvLeaderboard.layoutManager = layoutManager

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        leaderboardViewModel.getAllLeaderboard().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("LeaderboardFragment", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("LeaderboardFragment", "Success: ${result.data}")

                    val data = result.data
                    val adapter = LeaderboardAdapter()
                    adapter.submitList(data)
                    binding.rvLeaderboard.adapter = adapter
                }
                is Result.Error -> {
                    showLoading(false)
                    Log.e("LeaderboardFragment", "Error: ${result.error}")
                    if (result.error.contains("401")) {
                        showToast("Unauthorized, please re-login.")
                        leaderboardViewModel.logout()
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