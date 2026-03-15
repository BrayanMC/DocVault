package com.docvault.data.watermark

import android.graphics.Bitmap

/**
 * Contract for applying semi-transparent watermarks to document images.
 */
interface WatermarkManager {
    /**
     * Applies a semi-transparent watermark with [text] over [bitmap].
     * Returns a new [Bitmap] with the watermark applied.
     */
    fun applyWatermark(
        bitmap: Bitmap,
        text: String,
    ): Bitmap
}
