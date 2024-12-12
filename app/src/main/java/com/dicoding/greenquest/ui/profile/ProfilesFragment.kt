package com.dicoding.greenquest.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.databinding.FragmentProfilesBinding
import com.dicoding.greenquest.ui.customizeprofile.CustomizeProfileActivity

class ProfilesFragment : Fragment() {

    private var _binding: FragmentProfilesBinding? = null

    private val profilesViewModel: ProfilesViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfilesBinding.inflate(inflater, container, false)

        profilesViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.textUsername.text = "Hey ${user.name}"
            binding.textName.text = "${user.name}"
            binding.textCardUsername.text = "${user.username}"
            Glide.with(binding.root.context)
                .load(user.image)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder))
                .error(R.drawable.ic_error)
                .into(binding.ivAvatar)
            binding.pointsCount.text = "${user.points} points"
        }

        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLeaderboard.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_profiles_to_navigation_leaderboard,
                null,
                navOptions {
                    popUpTo(R.id.navigation_profiles) {
                        inclusive = true // Menghapus ProfilesFragment dari stack
                    }
                }
            )
        }
        binding.btnLihatProfile.setOnClickListener {
            val intent = Intent(requireContext(), CustomizeProfileActivity::class.java)
            startActivity(intent)
        }

        // Example: Set click listener for the button
        binding.logoutBtn.setOnClickListener {
            profilesViewModel.logout()
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHistory.layoutManager = layoutManager

        observeViewModel()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        profilesViewModel.getQuest().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    Log.d("HomeFragment", "Loading...")
                }
                is Result.Success -> {
                    showLoading(false)
                    Log.d("HomeFragment", "Success: ${result.data}")

                    val data = result.data


                    // Filter quest yang belum selesai
                    val completeQuests = data.filter { it.isCompleted }

                    if (completeQuests.isEmpty()) {
                        // Jika semua quest selesai, tampilkan pesan
                        binding.rvHistory.visibility = View.GONE
                        binding.tvNoQuestMessage.apply {
                            visibility = View.VISIBLE
                            text = "Anda Belum menyelesaikan quest satupun hari ini"
                        }
                    } else {
                        // Tampilkan RecyclerView jika ada quest yang belum selesai
                        binding.rvHistory.visibility = View.VISIBLE
                        binding.tvNoQuestMessage.visibility = View.GONE

                        val adapter = HistoryAdapter()
                        adapter.submitList(completeQuests)
                        binding.rvHistory.adapter = adapter
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    Log.e("HomeFragment", "Error: ${result.error}")
                    if (result.error.contains("401")) {
                        showToast("Unauthorized, please re-login.")
                        profilesViewModel.logout()
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