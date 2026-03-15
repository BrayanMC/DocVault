package com.docvault.feature.documents.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.docvault.core.common.result.DocVaultResult
import com.docvault.core.ui.base.BaseViewModel
import com.docvault.data.filesystem.DocumentFileManager
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.domain.usecase.AddDocumentUseCase
import com.docvault.domain.usecase.DeleteDocumentUseCase
import com.docvault.domain.usecase.GetDocumentsUseCase
import com.docvault.feature.documents.presentation.state.DocumentsUiState
import com.docvault.lib.location.DocVaultLocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val addDocumentUseCase: AddDocumentUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val locationManager: DocVaultLocationManager,
    private val documentFileManager: DocumentFileManager,
) : BaseViewModel<DocumentsUiState>(DocumentsUiState.Loading) {
    init {
        loadDocuments()
    }

    fun loadDocuments() {
        viewModelScope.launch {
            getDocumentsUseCase().collect { result ->
                when (result) {
                    is DocVaultResult.Success ->
                        updateState(
                            DocumentsUiState.Success(result.data),
                        )

                    is DocVaultResult.Error ->
                        updateState(
                            DocumentsUiState.Error(result.throwable.message ?: "Error"),
                        )

                    DocVaultResult.Loading -> updateState(DocumentsUiState.Loading)
                }
            }
        }
    }

    fun filterByType(type: DocumentType?) {
        viewModelScope.launch {
            getDocumentsUseCase(type).collect { result ->
                when (result) {
                    is DocVaultResult.Success ->
                        updateState(
                            DocumentsUiState.Success(result.data),
                        )

                    is DocVaultResult.Error ->
                        updateState(
                            DocumentsUiState.Error(result.throwable.message ?: "Error"),
                        )

                    DocVaultResult.Loading -> updateState(DocumentsUiState.Loading)
                }
            }
        }
    }

    fun addDocument(uri: Uri) {
        viewModelScope.launch {
            updateState(DocumentsUiState.Loading)

            val mimeType = documentFileManager.getMimeType(uri)
            val documentType =
                when {
                    mimeType?.contains("pdf") == true -> DocumentType.PDF
                    else -> DocumentType.IMAGE
                }
            val fileName = documentFileManager.getFileName(uri)
            val bytes =
                documentFileManager.readBytes(uri) ?: run {
                    updateState(DocumentsUiState.Error("No se pudo leer el archivo"))
                    return@launch
                }

            val locationResult = runCatching { locationManager.getCurrentLocation() }.getOrNull()
            val address =
                locationResult?.let {
                    runCatching {
                        locationManager.getAddressFromCoordinates(it.latitude, it.longitude)
                    }.getOrNull()
                }

            val document =
                Document(
                    id = UUID.randomUUID().toString(),
                    name = fileName,
                    type = documentType,
                    filePath = "",
                    createdAt = System.currentTimeMillis(),
                    fileSize = bytes.size.toLong(),
                    latitude = locationResult?.latitude,
                    longitude = locationResult?.longitude,
                    address = address,
                )

            when (val result = addDocumentUseCase(document, bytes)) {
                is DocVaultResult.Success -> loadDocuments()
                is DocVaultResult.Error ->
                    updateState(
                        DocumentsUiState.Error(
                            result.throwable.message ?: "Error al agregar documento"
                        ),
                    )

                DocVaultResult.Loading -> Unit
            }
        }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            when (val result = deleteDocumentUseCase(document)) {
                is DocVaultResult.Success -> loadDocuments()
                is DocVaultResult.Error ->
                    emitError(
                        result.throwable.message ?: "Error al eliminar documento",
                    )

                DocVaultResult.Loading -> Unit
            }
        }
    }

    private fun getFileName(
        uri: Uri,
        context: Context,
    ): String {
        var name = "documento_${System.currentTimeMillis()}"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) {
                name = cursor.getString(index)
            }
        }
        return name
    }
}
