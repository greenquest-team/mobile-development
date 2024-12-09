package com.dicoding.greenquest.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.FragmentProfilesBinding
import com.dicoding.greenquest.ui.CustomizeProfileActivity
import com.dicoding.greenquest.ui.home.HomeViewModel

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilesViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.textUsername.text = "Hey ${user.name}"
            binding.textName.text = "${user.name}"
            binding.textCardUsername.text = "${user.username}"
        }

        // Example: Set points count dynamically
        binding.pointsCount.text = "1200 Points" // Replace with actual points data

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}