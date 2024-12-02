package com.dicoding.greenquest.ui.scan

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.greenquest.OverlayView
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.ActivityScanBinding
import com.dicoding.greenquest.helper.ObjectDetectorHelper
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.concurrent.Executors
import com.dicoding.greenquest.data.Result

class ScanActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CameraActivity"
    }

    private val showViewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityScanBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    private lateinit var label: String
    private var isCameraRunning: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetScan.setOnClickListener {
            startCamera()
            binding.btnResetScan.visibility = View.GONE
            binding.overlay.clear()
            viewCardTextClose()
            showLoading(false)
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        // Pastikan kamera dihentikan saat aktivitas dihentikan sementara
        stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Bersihkan resource yang digunakan
        stopCamera()
    }

    private fun startCamera() {
        isCameraRunning = true

        objectDetectorHelper = ObjectDetectorHelper(
            context = this,
            detectorListener = object : ObjectDetectorHelper.DetectorListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@ScanActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onResults(results: MutableList<Detection>?, inferenceTime: Long, imageHeight: Int, imageWidth: Int) {
                    runOnUiThread {
                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)

//                                disini letak untuk menaruh custom view
                                binding.overlay.setResults(
                                    results, imageHeight, imageWidth
                                )
//
                            } else {
                                binding.overlay.clear()
//                                binding.tvInferenceTime.text = ""
                            }
                        }

                        // Force a redraw
                        binding.overlay.invalidate()

                    }
                }
            }
        )

        binding.overlay.setOnBoxClickListener(object : OverlayView.OnBoxClickListener {
            override fun onBoxClicked(detection: Detection) {
                if (!isCameraRunning) {
                    // Jika kamera tidak aktif, abaikan klik
                    return
                }

                // Jalankan stopCamera saat kotak diklik
                stopCamera()
                binding.btnResetScan.visibility = View.VISIBLE

                // Tampilkan informasi kotak yang diklik
                label = detection.categories[0].label

                viewCardTextShow()
            }
        })


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
            // The analyzer can then be assigned to the instance
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                objectDetectorHelper.detectObject(image)
            }

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.viewFinder.surfaceProvider
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            } catch (e: Exception) {
                showToast("Gagal memunculkan kamera.")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        isCameraRunning = false
        ProcessCameraProvider.getInstance(this).get().unbindAll()
        binding.overlay.setOnBoxClickListener(null)
    }

    private fun viewCardTextShow() {

        label = "plastic"

        showViewModel.randomWasteType(label).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showToast(" materi $label, berhasil di get. Silahkan scroll ke bawah")
                    val randomItem = result.data

                    binding.allTextCard.visibility = View.VISIBLE
                    binding.greenCard.visibility = View.VISIBLE
                    binding.pinkCard.visibility = View.VISIBLE
                    binding.tvGreenCard.visibility = View.VISIBLE
                    binding.tvPinkCard.visibility = View.VISIBLE

                    binding.tvGreenCard.text = randomItem.description
                    binding.tvPinkCard.text = randomItem.craft

                    Log.d("RandomWaste", "Type Name: ${randomItem.typeName}")
                }

                is Result.Error -> {
                    showLoading(false)
                    Log.e("RandomWaste", "Error: ${result.error}")
                }
            }
        }
    }

    private fun viewCardTextClose() {
        binding.allTextCard.visibility = View.GONE
        binding.greenCard.visibility = View.GONE
        binding.pinkCard.visibility = View.GONE
        binding.tvGreenCard.visibility = View.GONE
        binding.tvPinkCard.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}