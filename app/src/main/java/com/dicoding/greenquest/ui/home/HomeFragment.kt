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
import com.dicoding.greenquest.R
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

        // Observasi data dari ViewModel
        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.btnLihat.text = it
        }

        // Aksi pada tombol "Lihat"
//        binding.btnLihat.setOnClickListener {
//            val intent = Intent(requireContext(), ScanActivity::class.java)
//            startActivity(intent)
//        }
        // Tambahkan logika lainnya untuk elemen seperti avatar, logo, dsb.
        binding.ivLogo.setImageResource(R.drawable.greenquest_logo_edited_transparent)
        binding.ivAvatar.setImageResource(R.drawable.ic_person_24) // Contoh jika ada gambar avatar

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}