package com.docvault.lib.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.docvault.lib.camera.model.CameraResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CameraManager {
    override suspend fun capturePhoto(): CameraResult =
        suspendCancellableCoroutine { continuation ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val imageCapture =
                    ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                val outputFile =
                    File(
                        context.cacheDir,
                        "capture_${System.currentTimeMillis()}.jpg",
                    )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as androidx.lifecycle.LifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        imageCapture,
                    )
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                    return@addListener
                }

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val bytes = outputFile.readBytes()
                            outputFile.delete()
                            continuation.resume(
                                CameraResult(
                                    filePath = output.savedUri?.path ?: "",
                                    bytes = bytes,
                                ),
                            )
                        }

                        override fun onError(exception: ImageCaptureException) {
                            continuation.resumeWithException(exception)
                        }
                    },
                )
            }, ContextCompat.getMainExecutor(context))
        }
}
