package com.dicoding.greenquest.helper

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.dicoding.greenquest.R
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
class ObjectDetectorHelper (
    var threshold: Float = 0.5f,
    var maxResults: Int = 5,
    val modelName: String = "efficientdet_lite0_v1.tflite",
    val context: Context,
    val detectorListener: DetectorListener?
) {
    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    private var objectDetector: ObjectDetector? = null

    init {
        setupObjectDetector()
    }


    private fun setupObjectDetector() {
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder()

        baseOptionsBuilder.setNumThreads(4)

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())


        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
            Log.d(TAG, "Setting up Object Detector with model: $modelName")

        } catch (e: IllegalStateException) {
            detectorListener?.onError(context.getString(R.string.failed))
            Log.e(TAG, e.message.toString())
        }

    }

    fun detectObject(image: ImageProxy) {

        if (objectDetector == null) {
            setupObjectDetector()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-image.imageInfo.rotationDegrees / 90)) // Rotasi
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR)) // Resize ke [224, 224]
            .add(NormalizeOp(0.0f, 1.0f)) // Normalisasi ke [-1, 1]
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitmap(image)))

        // Perform inference
        var inferenceTime = SystemClock.uptimeMillis()
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

//        // Proses hasil inferensi
//        results?.forEach { detection ->
//            val boundingBox = detection.boundingBox // Koordinat bounding box
//            val categories = detection.categories  // Informasi kategori dan skor
//
//            // Log hasil deteksi objek
//            categories.forEach { category ->
//                Log.d(TAG, "Label: ${category.label}, Score: ${category.score}")
//            }
//
//            // Optional: Log informasi bounding box (misalnya koordinat)
//            Log.d(TAG, "BoundingBox: $boundingBox")
//        }


        detectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val bitmapBuffer = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    companion object {
        private const val TAG = "ObjectDetectorHelper"
    }
}