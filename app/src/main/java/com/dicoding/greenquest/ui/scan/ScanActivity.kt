package com.dicoding.greenquest.ui.scan

import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.greenquest.ViewModelFactory
import com.dicoding.greenquest.databinding.ActivityScanBinding
import java.util.concurrent.Executors
import com.dicoding.greenquest.data.Result
import com.dicoding.greenquest.data.local.entity.QuestEntity
import com.dicoding.greenquest.helper.ImageClassifierHelper
import com.dicoding.greenquest.ui.main.MainActivity
import com.google.common.util.concurrent.ListenableFuture
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class ScanActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CameraActivity"
        const val QUEST_EXTRA = "quest_extra"
    }

    private val showViewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var binding: ActivityScanBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private lateinit var label: String
    private var isCameraRunning: Boolean = true

    private val labelToIndonesian = mapOf(
        "metal" to "baja/metal",
        "paper" to "kertas",
        "plastic" to "plastik",
        "cardboard" to "kardus",
        "glass" to "kaca"
    )

    private var isFlashlightOn: Boolean = false
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String

    private var userID = 0
    private var points = 0

    private lateinit var quest: QuestEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quest = intent.getParcelableExtra(QUEST_EXTRA)!!

        showViewModel.getSession().observe(this) { user ->
            userID = user.user_id
            points = user.points
        }

        binding.fabChecklist.setOnClickListener {
            showAlert("Yeay", "kamu berhasil scan dan mendapatkan ilmu baru, kian hari kamu makin pintar :D")
        }

        when (quest.typeName) {
            "metal" -> {
                binding.objective.text = "Objective: Scan sampah Baja/metal"
            }
            "paper" -> {
                binding.objective.text = "Objective: Scan sampah kertas"
            }
            "plastic" -> {
                binding.objective.text = "Objective: Scan sampah plastik"
            }
            "cardboard" -> {
                binding.objective.text = "Objective: Scan sampah kardus"
            }
            "glass" -> {
                binding.objective.text = "Objective: Scan sampah kaca"
            }
        }

        binding.btnResetScan.visibility = View.GONE

        binding.btnResetScan.setOnClickListener {
            startCamera()
            binding.btnResetScan.visibility = View.GONE
            binding.btnScan.visibility = View.VISIBLE
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

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                       showToast("error")
                    }
                }
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val sortedCategories =
                                    it[0].categories.sortedByDescending { it?.score }
                                label = sortedCategories[0].label
                                val displayResult =
                                    sortedCategories.joinToString("\n") {
                                        val translatedLabel = labelToIndonesian[it.label] ?: it.label
                                        "$translatedLabel " + NumberFormat.getPercentInstance().format(it.score).trim()
                                    }
                                binding.tvResult.text = displayResult
                                binding.tvInferenceTime.text = "$inferenceTime ms"
                            } else {
                                binding.tvResult.text = ""
                                binding.tvInferenceTime.text = ""
                            }
                        }
                    }
                }
            }
        )

        binding.btnScan.setOnClickListener {
            if (label.isNotEmpty()) {
                if (label == quest.typeName) {
                    stopCamera()
                    binding.btnResetScan.visibility = View.VISIBLE
                    binding.btnScan.visibility = View.GONE

                    viewCardTextShow()
                } else {
                    showAlert("Maaf", "Hasil scan anda tidak sesuai dengan quest")
                }
            } else {
                showToast("Tidak ada hasil klasifikasi. Coba lagi.")
            }

        }


        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val rotation = binding.viewFinder.display?.rotation ?: Surface.ROTATION_0
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
            // The analyzer can then be assigned to the instance
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                imageClassifierHelper.classifyImage(image)
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
        ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.get().unbindAll()

        try {
            cameraManager.setTorchMode(cameraId, false)
            isFlashlightOn = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isFlashlightAvailable(): Boolean {
        val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        return cameraManager.cameraIdList.any { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    private fun viewCardTextShow() {

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
                    binding.fabChecklist.visibility = View.VISIBLE

                    binding.tvGreenCard.text = randomItem.description
                    binding.tvPinkCard.text = randomItem.craft

                    Log.d("RandomWaste", "Type Name: ${randomItem.typeName}")
                }

                is Result.Error -> {
                    showLoading(false)
                    showToast("Error")
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
        binding.fabChecklist.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.viewLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAlert(title: String, message: String) {

        if (title == "Error" || title == "Maaf") {
            AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("OK", null)
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("Yeah") { _, _ ->
                    showViewModel.updateQuest(quest)
                    updateUserPoints(userID, points, quest.pointsAwarded)
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun updateUserPoints(userId: Int, points: Int, newPoints: Int) {
        showViewModel.updateUserPoints(userId, points, newPoints)
    }
}