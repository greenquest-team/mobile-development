package com.dicoding.greenquest.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.dicoding.greenquest.R
import com.dicoding.greenquest.databinding.FragmentProfilesBinding
import com.dicoding.greenquest.ui.CustomizeProfileActivity

class ProfilesFragment : Fragment() {

    // View binding property for fragment_profiles.xml
    private var _binding: FragmentProfilesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example: Set username text dynamically
        binding.textCardUsername.text = "@ray132" // Replace with actual user data

        // Example: Set points count dynamically
        binding.pointsCount.text = "1200 Points" // Replace with actual points data

        // Example: Set click listener for the button
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
