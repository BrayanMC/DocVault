package com.docvault.feature.detail.presentation.state

import com.docvault.domain.model.AccessLog
import com.docvault.domain.model.Document

sealed class DetailUiState {
    data object Loading : DetailUiState()

    data object Deleted : DetailUiState()

    data class Success(
        val document: Document,
        val accessLogs: List<AccessLog> = emptyList(),
        val decryptedBytes: ByteArray? = null,
    ) : DetailUiState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return document == other.document &&
                accessLogs == other.accessLogs &&
                decryptedBytes.contentEquals(other.decryptedBytes)
        }

        override fun hashCode(): Int {
            var result = document.hashCode()
            result = 31 * result + accessLogs.hashCode()
            result = 31 * result + (decryptedBytes?.contentHashCode() ?: 0)
            return result
        }
    }

    data class Error(val message: String) : DetailUiState()
}
