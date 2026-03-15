package com.docvault.core.ui.widget

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class ZoomableImageView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : AppCompatImageView(context, attrs, defStyleAttr) {
        companion object {
            private const val MIN_ZOOM = 1.0f
            private const val MAX_ZOOM = 5.0f
        }

        private val matrix = Matrix()
        private var scaleFactor = 1.0f
        private var lastTouchX = 0f
        private var lastTouchY = 0f
        private var isDragging = false

        private val scaleGestureDetector =
            ScaleGestureDetector(
                context,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        val scale = detector.scaleFactor
                        scaleFactor = max(MIN_ZOOM, min(scaleFactor * scale, MAX_ZOOM))
                        matrix.setScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                        imageMatrix = matrix
                        return true
                    }
                },
            )

        init {
            scaleType = ScaleType.MATRIX
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            scaleGestureDetector.onTouchEvent(event)

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    isDragging = false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!scaleGestureDetector.isInProgress) {
                        val dx = event.x - lastTouchX
                        val dy = event.y - lastTouchY
                        matrix.postTranslate(dx, dy)
                        imageMatrix = matrix
                        lastTouchX = event.x
                        lastTouchY = event.y
                        isDragging = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) performClick()
                }
            }
            return true
        }

        override fun performClick(): Boolean {
            super.performClick()
            return true
        }

        fun resetZoom() {
            scaleFactor = 1.0f
            matrix.reset()
            imageMatrix = matrix
        }
    }
