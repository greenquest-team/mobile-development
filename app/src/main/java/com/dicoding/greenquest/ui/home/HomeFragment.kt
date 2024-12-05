package com.dicoding.greenquest.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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

    private var animatorSet: AnimatorSet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Aksi pada tombol "Lihat"
//        binding.btnLihat.setOnClickListener {
//            val intent = Intent(requireContext(), ScanActivity::class.java)
//            startActivity(intent)
//        }

        binding.circularProgressBar.progressMax = 100f

        val progressBarAnimator = ObjectAnimator.ofFloat(binding.circularProgressBar, "progress", 0f, 50f).apply {
            duration = 1000 // 1 second
            interpolator = AccelerateDecelerateInterpolator() // Smooth easing
        }

        val textAnimator = ValueAnimator.ofInt(0, 50).apply {
            duration = 1000 // 1 second
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                binding.progressText.text = "$progress%" // Update text dynamically
            }
        }

        // Combine both animations into an AnimatorSet
        animatorSet = AnimatorSet().apply {
            playTogether(progressBarAnimator, textAnimator)
            start()// Play both animations simultaneously
        }


        return root
    }

    override fun onPause() {
        super.onPause()
        animatorSet?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        animatorSet?.cancel() // Batalkan animasi untuk mencegah error
        animatorSet = null // Bersihkan referensi untuk menghindari memory leak
    }
}