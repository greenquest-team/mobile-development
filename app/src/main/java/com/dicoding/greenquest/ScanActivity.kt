package com.dicoding.greenquest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.greenquest.databinding.ActivityScanBinding
import com.dicoding.greenquest.helper.ObjectDetectorHelper
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CameraActivity"
    }

    private lateinit var binding: ActivityScanBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetScan.setOnClickListener {
            startCamera()
            binding.btnResetScan.visibility = View.GONE
            binding.overlay.clear()
            viewCardTextClose()
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
                // Jalankan stopCamera saat kotak diklik
                stopCamera()
                binding.btnResetScan.visibility = View.VISIBLE
                viewCardTextShow()

                // Tampilkan informasi kotak yang diklik
                val label = detection.categories[0].label
                val score = detection.categories[0].score
                Toast.makeText(
                    this@ScanActivity,
                    "Kotak '$label' dengan skor ${score * 100}% diklik",
                    Toast.LENGTH_SHORT
                ).show()
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

            } catch (exc: Exception) {
                Toast.makeText(
                    this@ScanActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        ProcessCameraProvider.getInstance(this).get().unbindAll()
    }

    private fun viewCardTextShow() {
        binding.allTextCard.visibility = View.VISIBLE
        binding.greenCard.visibility = View.VISIBLE
        binding.pinkCard.visibility = View.VISIBLE
        binding.tvGreenCard.visibility = View.VISIBLE
        binding.tvPinkCard.visibility = View.VISIBLE
    }

    private fun viewCardTextClose() {
        binding.allTextCard.visibility = View.GONE
        binding.greenCard.visibility = View.GONE
        binding.pinkCard.visibility = View.GONE
        binding.tvGreenCard.visibility = View.GONE
        binding.tvPinkCard.visibility = View.GONE
    }
}