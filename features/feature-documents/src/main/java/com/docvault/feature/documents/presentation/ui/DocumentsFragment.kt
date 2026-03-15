package com.docvault.feature.documents.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.docvault.core.common.extensions.hide
import com.docvault.core.common.extensions.show
import com.docvault.core.common.extensions.showIf
import com.docvault.core.navigation.NavigationCommand
import com.docvault.core.navigation.Navigator
import com.docvault.core.ui.base.BaseFragment
import com.docvault.core.ui.permissions.PermissionManager
import com.docvault.core.ui.permissions.PermissionManagerImpl
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.feature.documents.R
import com.docvault.feature.documents.databinding.BottomSheetAddDocumentBinding
import com.docvault.feature.documents.databinding.FragmentDocumentsBinding
import com.docvault.feature.documents.presentation.state.DocumentsUiState
import com.docvault.feature.documents.presentation.viewmodel.DocumentsViewModel
import com.docvault.lib.camera.ui.CameraFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocumentsFragment : BaseFragment<FragmentDocumentsBinding>() {
    private val viewModel: DocumentsViewModel by viewModels()
    private lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazy {
        DocumentsAdapter(
            onDocumentClick = { document ->
                navigator.navigate(NavigationCommand.ToDetail(document.id))
            },
            onDocumentLongClick = { document -> showDeleteConfirmation(document) },
        )
    }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            uri?.let { viewModel.addDocument(it) }
        }

    private val pdfLauncher =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let { viewModel.addDocument(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager = PermissionManagerImpl(this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentDocumentsBinding = FragmentDocumentsBinding.inflate(inflater, container, false)

    override fun initViews() {
        setupRecyclerView()
        setupFilters()
        setupFab()
        setupCameraResult()
    }

    private fun setupCameraResult() {
        parentFragmentManager.setFragmentResultListener(
            CameraFragment.REQUEST_KEY,
            viewLifecycleOwner,
        ) { _, bundle ->
            val uriString =
                bundle.getString(CameraFragment.KEY_URI) ?: return@setFragmentResultListener
            val uri = uriString.toUri()
            viewModel.addDocument(uri)
        }
    }

    override fun observeState() {
        collectOnStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    DocumentsUiState.Loading -> showLoading()
                    is DocumentsUiState.Success -> showDocuments(state)
                    is DocumentsUiState.Error -> showError(state.message)
                }
            }
        }
        collectOnStarted {
            viewModel.error.collect { message -> showError(message) }
        }
    }

    private fun setupRecyclerView() {
        binding.rvDocuments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DocumentsFragment.adapter
        }
    }

    private fun setupFilters() {
        with(binding) {
            chipAll.isChecked = true
            chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
                viewModel.filterByType(
                    when {
                        checkedIds.contains(chipPdf.id) -> DocumentType.PDF
                        checkedIds.contains(chipImage.id) -> DocumentType.IMAGE
                        else -> null
                    },
                )
            }
        }
    }

    private fun setupFab() {
        binding.fabAddDocument.setOnClickListener {
            showAddDocumentOptions()
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.show()
            rvDocuments.hide()
            tvEmptyState.hide()
            tvError.hide()
        }
    }

    private fun showDocuments(state: DocumentsUiState.Success) {
        with(binding) {
            progressBar.hide()
            tvError.hide()
            rvDocuments.showIf(state.documents.isNotEmpty())
            tvEmptyState.showIf(state.documents.isEmpty())
            if (state.documents.isNotEmpty()) {
                adapter.submitList(state.documents)
            }
        }
    }

    private fun showError(message: String) {
        with(binding) {
            progressBar.hide()
            rvDocuments.hide()
            tvEmptyState.hide()
            tvError.show()
            tvError.text = message
        }
    }

    private fun showAddDocumentOptions() {
        val bottomSheetBinding = BottomSheetAddDocumentBinding.inflate(layoutInflater)
        BottomSheetDialog(requireContext()).apply {
            setContentView(bottomSheetBinding.root)
            with(bottomSheetBinding) {
                btnGallery.setOnClickListener {
                    dismiss()
                    requestGalleryPermission(openPdf = false)
                }
                btnPdf.setOnClickListener {
                    dismiss()
                    requestGalleryPermission(openPdf = true)
                }
                btnCamera.setOnClickListener {
                    dismiss()
                    requestCameraPermission()
                }
            }
            show()
        }
    }

    private fun requestGalleryPermission(openPdf: Boolean) {
        permissionManager.request(
            permissions = listOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            onGranted = { if (openPdf) openPdfPicker() else openGallery() },
            onDenied = { if (openPdf) openPdfPicker() else openGallery() },
        )
    }

    private fun requestCameraPermission() {
        permissionManager.request(
            permissions =
                listOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                ),
            onGranted = { openCamera() },
            onDenied = { showPermissionDenied() },
        )
    }

    private fun openGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
    }

    private fun openPdfPicker() {
        pdfLauncher.launch(arrayOf("application/pdf"))
    }

    private fun openCamera() {
        findNavController().navigate(
            R.id.action_documents_to_camera,
        )
    }

    private fun showPermissionDenied() {
        Toast.makeText(
            requireContext(),
            getString(R.string.documents_permission_denied),
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun showDeleteConfirmation(document: Document) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.documents_delete_title)
            .setMessage(getString(R.string.documents_delete_message, document.name))
            .setPositiveButton(R.string.documents_delete_confirm) { _, _ ->
                viewModel.deleteDocument(document)
            }
            .setNegativeButton(R.string.documents_cancel, null)
            .show()
    }
}
