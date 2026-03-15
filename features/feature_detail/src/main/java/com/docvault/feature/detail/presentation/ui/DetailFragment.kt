package com.docvault.feature.detail.presentation.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.createBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.docvault.core.common.extensions.hide
import com.docvault.core.common.extensions.show
import com.docvault.core.navigation.NavigationArgs
import com.docvault.core.ui.base.BaseFragment
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.feature.detail.R
import com.docvault.feature.detail.databinding.FragmentDetailBinding
import com.docvault.feature.detail.presentation.state.DetailUiState
import com.docvault.feature.detail.presentation.viewmodel.DetailViewModel
import com.docvault.lib.security.biometric.BiometricAuthManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    private val viewModel: DetailViewModel by viewModels()

    @Inject
    lateinit var biometricAuthManager: BiometricAuthManager

    private val accessLogAdapter by lazy { AccessLogAdapter() }

    private var documentId: String = ""

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentDetailBinding = FragmentDetailBinding.inflate(inflater, container, false)

    override fun initViews() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        documentId = arguments?.getString(NavigationArgs.ARG_DOCUMENT_ID) ?: return
        setupToolbar()
        setupRecyclerView()
        authenticateAndLoad()
    }

    override fun observeState() {
        collectOnStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DetailUiState.Loading -> showLoading()
                    is DetailUiState.Success -> showContent(state)
                    is DetailUiState.Deleted -> navigateBack()
                    is DetailUiState.Error -> showError(state.message)
                }
            }
        }
        collectOnStarted {
            viewModel.error.collect { message -> showError(message) }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            title = getString(R.string.detail_title)
            showNavigationIcon(true)
            onNavigationClick = { navigateBack() }
        }
    }

    private fun setupRecyclerView() {
        binding.rvAccessLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = accessLogAdapter
        }
    }

    private fun authenticateAndLoad() {
        biometricAuthManager.authenticate(
            activity = requireActivity(),
            title = getString(R.string.detail_authentication_title),
            subtitle = getString(R.string.detail_authentication_subtitle),
            onSuccess = { viewModel.loadDocument(documentId) },
            onError = { _, message -> showError(message) },
        )
    }

    private fun showLoading() {
        with(binding) {
            loadingView.show()
            contentView.hide()
            errorView.hide()
        }
    }

    private fun showContent(state: DetailUiState.Success) {
        with(binding) {
            loadingView.hide()
            errorView.hide()
            contentView.show()
            tvDocumentName.text = state.document.name
            tvDocumentAddress.text = state.document.address
                ?: getString(R.string.detail_location_unavailable)
            accessLogAdapter.submitList(state.accessLogs)
            setupDeleteButton(state.document)
            loadDocumentImage(state)
        }
    }

    private fun showError(message: String) {
        with(binding) {
            loadingView.hide()
            contentView.hide()
            errorView.show()
            errorView.message = message
            errorView.onRetryClick = { authenticateAndLoad() }
        }
    }

    private fun loadDocumentImage(state: DetailUiState.Success) {
        val bytes = state.decryptedBytes ?: return
        when (state.document.type) {
            DocumentType.IMAGE -> loadImage(bytes, state)
            DocumentType.PDF -> loadPdf(bytes, state)
        }
    }

    private fun loadImage(
        bytes: ByteArray,
        state: DetailUiState.Success,
    ) {
        Glide.with(this)
            .asBitmap()
            .load(bytes)
            .into(
                object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        if (!isAdded) return
                        val watermarkText = buildWatermarkText(state.document)
                        val watermarked = viewModel.applyWatermark(resource, watermarkText)
                        binding.ivDocument.setImageBitmap(watermarked)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        if (!isAdded) return
                        binding.ivDocument.setImageDrawable(placeholder)
                    }
                },
            )
    }

    private fun loadPdf(
        bytes: ByteArray,
        state: DetailUiState.Success,
    ) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val tempFile = File(requireContext().cacheDir, "temp_${System.currentTimeMillis()}.pdf")
            tempFile.writeBytes(bytes)
            val parcelFileDescriptor =
                ParcelFileDescriptor.open(
                    tempFile,
                    ParcelFileDescriptor.MODE_READ_ONLY,
                )
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val page = pdfRenderer.openPage(0)
            val bitmap = createBitmap(page.width, page.height)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pdfRenderer.close()
            parcelFileDescriptor.close()
            tempFile.delete()

            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                val watermarkText = buildWatermarkText(state.document)
                val watermarked = viewModel.applyWatermark(bitmap, watermarkText)
                binding.ivDocument.setImageBitmap(watermarked)
            }
        }
    }

    private fun buildWatermarkText(document: Document): String {
        return document.address ?: document.run {
            when {
                latitude != null && longitude != null -> "$latitude, $longitude"
                else -> "DocVault"
            }
        }
    }

    private fun setupDeleteButton(document: Document) {
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation(document)
        }
    }

    private fun showDeleteConfirmation(document: Document) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.detail_delete_title)
            .setMessage(getString(R.string.detail_delete_message, document.name))
            .setPositiveButton(R.string.detail_delete_button) { _, _ ->
                biometricAuthManager.authenticate(
                    activity = requireActivity(),
                    title = getString(R.string.detail_delete_confirm_title),
                    subtitle = getString(R.string.detail_delete_confirm_subtitle),
                    onSuccess = { viewModel.deleteDocument(document) },
                    onError = { _, message -> showError(message) },
                )
            }
            .setNegativeButton(R.string.detail_cancel_button, null)
            .show()
    }

    private fun navigateBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
