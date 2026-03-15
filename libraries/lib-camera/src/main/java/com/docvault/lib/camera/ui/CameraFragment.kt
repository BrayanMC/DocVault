package com.docvault.lib.camera.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.docvault.core.ui.base.BaseFragment
import com.docvault.lib.camera.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {
    private var imageCapture: ImageCapture? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)

    override fun initViews() {
        startCamera()
        binding.btnCapture.setOnClickListener { takePhoto() }
        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun observeState() = Unit

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview =
                Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture =
                ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                )
            } catch (e: Exception) {
                parentFragmentManager.popBackStack()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val outputFile =
            File(
                requireContext().cacheDir,
                "capture_${System.currentTimeMillis()}.jpg",
            )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = output.savedUri ?: Uri.fromFile(outputFile)
                    setFragmentResult(
                        REQUEST_KEY,
                        bundleOf(KEY_URI to uri.toString()),
                    )
                    if (isAdded) {
                        parentFragmentManager.popBackStack()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    if (isAdded) {
                        parentFragmentManager.popBackStack()
                    }
                }
            },
        )
    }

    companion object {
        const val REQUEST_KEY = "camera_request"
        const val KEY_URI = "camera_uri"
    }
}
