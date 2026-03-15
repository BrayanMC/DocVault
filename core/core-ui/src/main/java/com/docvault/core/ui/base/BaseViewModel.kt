package com.docvault.core.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docvault.core.common.result.DocVaultResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S>(initialState: S) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()

    protected fun updateState(state: S) {
        _uiState.value = state
    }

    protected fun emitError(message: String) {
        viewModelScope.launch {
            _error.emit(message)
        }
    }

    protected fun <T> handleResult(
        result: DocVaultResult<T>,
        onSuccess: (T) -> Unit,
    ) {
        when (result) {
            is DocVaultResult.Success -> onSuccess(result.data)
            is DocVaultResult.Error -> emitError(result.message ?: "Unknown error")
            is DocVaultResult.Loading -> Unit
        }
    }
}
