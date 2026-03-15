package com.docvault.core.common.extensions

import com.docvault.core.common.result.DocVaultResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

suspend fun <T> safeCall(block: suspend () -> T): DocVaultResult<T> =
    try {
        DocVaultResult.Success(block())
    } catch (e: Exception) {
        DocVaultResult.Error(e)
    }

fun <T> Flow<T>.asResult(): Flow<DocVaultResult<T>> =
    map<T, DocVaultResult<T>> { DocVaultResult.Success(it) }
        .onStart { emit(DocVaultResult.Loading) }
        .catch { emit(DocVaultResult.Error(it)) }
