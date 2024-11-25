package com.dicoding.greenquest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.greenquest.databinding.ActivityScanBinding
import com.dicoding.greenquest.helper.ObjectDetectorHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    }

    override fun onResume() {
        super.onResume()
        startCamera()
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

                                val detectedLabels = it.mapNotNull { detection ->
                                    detection.categories.firstOrNull()?.label
                                }

                                // Update tombol dinamis
                                updateButtons(detectedLabels)
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

    private fun updateButtons(detections: List<String>) {
        val buttonContainer = binding.buttonContainer
        buttonContainer.removeAllViews() // Hapus tombol lama


        var isCameraActive = true // Untuk melacak status kamera
        val activeColor = ContextCompat.getColor(this@ScanActivity, R.color.btn_scan_active)
        val defaultColor = ContextCompat.getColor(this@ScanActivity, R.color.btn_scan_inactive)

        var lastClickedButton: android.widget.Button? = null

        for (label in detections) {
            val button = android.widget.Button(this).apply {
                text = label
                setBackgroundColor(defaultColor) // Warna awal tombol

                setOnClickListener {
                    // Jika kamera aktif, matikan kamera dan ubah warna tombol
                    if (isCameraActive) {
                        stopCamera()
                        binding.view.visibility = View.VISIBLE
                        isCameraActive = false
                        setBackgroundColor(activeColor)

                        lastClickedButton?.setBackgroundColor(activeColor)

                        lastClickedButton = this

                    } else {
                        // Jika kamera tidak aktif, nyalakan kamera dan kembalikan warna tombol
                        startCamera()
                        binding.view.visibility = View.GONE
                        isCameraActive = true
                        setBackgroundColor(defaultColor)

                        setBackgroundColor(defaultColor)

                        // Reset tombol terakhir yang aktif
                        lastClickedButton = null
                    }
                }
            }
            buttonContainer.addView(button)
        }
    }

    private fun stopCamera() {
        ProcessCameraProvider.getInstance(this).get().unbindAll()
    }
}