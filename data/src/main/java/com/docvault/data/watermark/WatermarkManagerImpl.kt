package com.docvault.data.watermark

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.graphics.withRotation
import javax.inject.Inject
import kotlin.math.sqrt

class WatermarkManagerImpl @Inject constructor() : WatermarkManager {
    companion object {
        private const val WATERMARK_ALPHA = 80
        private const val TEXT_SIZE = 36f
        private const val ROTATION_DEGREES = -30f
        private const val REPEAT_SPACING = 250f
    }

    override fun applyWatermark(
        bitmap: Bitmap,
        text: String,
    ): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val paint =
            Paint().apply {
                color = Color.RED
                alpha = WATERMARK_ALPHA
                textSize = TEXT_SIZE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

        canvas.withRotation(ROTATION_DEGREES, result.width / 2f, result.height / 2f) {
            val textWidth = paint.measureText(text)
            val diagonal =
                sqrt(
                    (result.width * result.width + result.height * result.height).toDouble(),
                ).toFloat()

            var y = -diagonal / 2
            while (y < diagonal) {
                var x = -diagonal / 2
                while (x < diagonal) {
                    drawText(text, x, y, paint)
                    x += textWidth + REPEAT_SPACING
                }
                y += REPEAT_SPACING
            }

        }
        return result
    }
}
