package com.dicoding.greenquest.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.greenquest.databinding.FragmentProfilesBinding

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
        binding.tvUsername.text = "Ray" // Replace with actual user data

        // Example: Set points count dynamically
        binding.pointsCount.text = "1200 Points" // Replace with actual points data

        // Example: Set click listener for the button
        binding.button.setOnClickListener {
            // Handle button click, e.g., navigate to leaderboard
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
