package com.dicoding.greenquest.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.greenquest.ScanActivity
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.FragmentHomeBinding
import com.dicoding.greenquest.ui.login.LoginActivity
import com.dicoding.greenquest.ui.main.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val homeViewModel: HomeViewModel by viewModels {
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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        contoh aja
        val btnScan: TextView = binding.button
        homeViewModel.text.observe(viewLifecycleOwner) {
            btnScan.text = it
        }

        binding.logoutButton.setOnClickListener {
            homeViewModel.logout()
        }

        btnScan.setOnClickListener {
            val intent = Intent(requireContext(), ScanActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}