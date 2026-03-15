package com.docvault.lib.camera

import com.docvault.lib.camera.model.CameraResult

/**
 * Manages camera capture operations using CameraX.
 * Handles photo capture and returns the result as bytes and file path.
 */
interface CameraManager {
    suspend fun capturePhoto(): CameraResult
}
