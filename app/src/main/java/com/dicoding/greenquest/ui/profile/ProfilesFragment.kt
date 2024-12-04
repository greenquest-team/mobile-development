package com.dicoding.greenquest.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.greenquest.databinding.FragmentProfilesBinding

class ProfilesFragment : Fragment() {

    private var _binding: FragmentProfilesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfilesViewModel::class.java)

        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example: Set username text dynamically
        binding.tvUsername.text = "John Doe" // Replace with actual user data

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