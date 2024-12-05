package com.dicoding.greenquest.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.FragmentProfilesBinding
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

        // Example: Set username text dynamically
        binding.tvUsername.text = "Ray" // Replace with actual user data

        // Example: Set points count dynamically
        binding.pointsCount.text = "1200 Points" // Replace with actual points data

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