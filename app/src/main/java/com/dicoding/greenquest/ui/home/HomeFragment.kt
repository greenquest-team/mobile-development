package com.dicoding.greenquest.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.greenquest.ui.scan.ScanActivity
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.FragmentHomeBinding

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

        // Akses elemen-elemen di layout XML melalui binding
        binding.ivLogo  // ImageView untuk logo
        binding.ivAvatar // CircleImageView untuk avatar
        binding.tvWelcome // TextView untuk salam
        binding.tvQuestion // TextView untuk pertanyaan
        binding.view // View untuk bottom sheet
        binding.misi // View untuk card misi
        binding.tvMisiTitle // TextView untuk judul misi
        binding.tvQuestTitle // TextView untuk judul quest
        binding.circularProgressBar // CircularProgressBar
        binding.textView // TextView "lihat"

        // ... kode lainnya, seperti mengatur teks, event listener, dll. ...

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