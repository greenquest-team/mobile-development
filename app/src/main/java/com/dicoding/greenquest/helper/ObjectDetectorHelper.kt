package com.dicoding.greenquest.helper

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.dicoding.greenquest.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.FileInputStream
import java.nio.ByteBuffer

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
            detectorListener?.onError(context.getString(R.string.test))
            Log.e(TAG, e.message.toString())
        }

    }

    fun detectObject(image: ImageProxy) {

        if (objectDetector == null) {
            setupObjectDetector()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(Rot90Op(-image.imageInfo.rotationDegrees / 90))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(toBitmap(image)))

//        val imageProcessingOptions = ImageProcessingOptions.builder()
//            .setOrientation(getOrientationFromRotation(image.imageInfo.rotationDegrees))
//            .build()

        var inferenceTime = SystemClock.uptimeMillis()
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        detectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }

//    private fun getOrientationFromRotation(rotationDegrees: Int): ImageProcessingOptions.Orientation {
//
//        return when (rotationDegrees) {
//            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
//            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
//            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
//            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
//        }
//
//    }

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