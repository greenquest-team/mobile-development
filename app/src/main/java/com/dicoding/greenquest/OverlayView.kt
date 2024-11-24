package com.dicoding.greenquest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.tensorflow.lite.task.vision.detector.Detection
import java.text.NumberFormat
import java.util.LinkedList
import kotlin.math.max

class OverlayView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var results: List<Detection> = LinkedList<Detection>()
    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    private val boxColors = listOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
        Color.LTGRAY,
        Color.DKGRAY
    )

    fun setResults(
        detectionResults: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }

    init {
        initPaints()
    }

    private fun initPaints() {
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 8f

        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for ((index, result) in results.withIndex()) {
            val boundingBox = result.boundingBox

            val left = boundingBox.left * scaleFactor
            val top = boundingBox.top * scaleFactor
            val right = boundingBox.right * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor

            // Assign a color to the bounding box (cyclic or random)
            boxPaint.color = boxColors[index % boxColors.size]

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            // Create text to display alongside detected objects
            val drawableText = "${result.categories[0].label} " +
                    NumberFormat.getPercentInstance().format(result.categories[0].score)

            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + Companion.BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + Companion.BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            // Draw text for detected object
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
        }
    }

    fun clear() {
        boxPaint.reset()
        textBackgroundPaint.reset()
        textPaint.reset()
        invalidate()
        initPaints()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }

}