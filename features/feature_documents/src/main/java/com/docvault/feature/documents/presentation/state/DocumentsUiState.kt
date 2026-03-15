package com.docvault.feature.documents.presentation.state

import com.docvault.domain.model.Document

sealed class DocumentsUiState {
    data object Loading : DocumentsUiState()

    data class Success(val documents: List<Document>) : DocumentsUiState()

    data class Error(val message: String) : DocumentsUiState()
}
