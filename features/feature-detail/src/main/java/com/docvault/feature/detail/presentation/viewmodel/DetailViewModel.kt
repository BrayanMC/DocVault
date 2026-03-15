package com.docvault.feature.detail.presentation.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.docvault.core.common.result.DocVaultResult
import com.docvault.core.ui.base.BaseViewModel
import com.docvault.data.filesystem.SecureFileManager
import com.docvault.data.watermark.WatermarkManager
import com.docvault.domain.model.Document
import com.docvault.domain.usecase.DeleteDocumentUseCase
import com.docvault.domain.usecase.GetAccessLogsUseCase
import com.docvault.domain.usecase.GetDocumentDetailUseCase
import com.docvault.domain.usecase.RegisterAccessLogUseCase
import com.docvault.feature.detail.presentation.state.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDocumentDetailUseCase: GetDocumentDetailUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val getAccessLogsUseCase: GetAccessLogsUseCase,
    private val registerAccessLogUseCase: RegisterAccessLogUseCase,
    private val watermarkManager: WatermarkManager,
    private val secureFileManager: SecureFileManager,
) : BaseViewModel<DetailUiState>(DetailUiState.Loading) {
    fun loadDocument(documentId: String) {
        viewModelScope.launch {
            updateState(DetailUiState.Loading)
            val result = getDocumentDetailUseCase(documentId)
            when (result) {
                is DocVaultResult.Success -> {
                    val document = result.data
                    registerAccessLogUseCase(documentId)
                    val bytes =
                        runCatching {
                            secureFileManager.readFile(document.filePath)
                        }.getOrNull()
                    updateState(DetailUiState.Success(document, decryptedBytes = bytes))
                    loadAccessLogs(documentId)
                }

                is DocVaultResult.Error -> emitError(result.message ?: "Error")
                is DocVaultResult.Loading -> Unit
            }
        }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            handleResult(deleteDocumentUseCase(document)) {
                updateState(DetailUiState.Deleted)
            }
        }
    }

    fun applyWatermark(
        bitmap: Bitmap,
        text: String,
    ): Bitmap = watermarkManager.applyWatermark(bitmap, text)

    private fun loadAccessLogs(documentId: String) {
        getAccessLogsUseCase(documentId)
            .onEach { result ->
                handleResult(result) { logs ->
                    val current = uiState.value
                    if (current is DetailUiState.Success) {
                        updateState(current.copy(accessLogs = logs))
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
