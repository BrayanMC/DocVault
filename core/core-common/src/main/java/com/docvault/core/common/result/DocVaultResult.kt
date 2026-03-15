package com.docvault.core.common.result

sealed class DocVaultResult<out T> {
    data class Success<T>(val data: T) : DocVaultResult<T>()

    data class Error(
        val throwable: Throwable,
        val message: String? = throwable.localizedMessage,
    ) : DocVaultResult<Nothing>()

    data object Loading : DocVaultResult<Nothing>()
}

val DocVaultResult<*>.isSuccess: Boolean
    get() = this is DocVaultResult.Success

val DocVaultResult<*>.isError: Boolean
    get() = this is DocVaultResult.Error

fun <T> DocVaultResult<T>.getOrNull(): T? = (this as? DocVaultResult.Success)?.data

inline fun <T, R> DocVaultResult<T>.map(crossinline transform: (T) -> R): DocVaultResult<R> =
    when (this) {
        is DocVaultResult.Success -> DocVaultResult.Success(transform(data))
        is DocVaultResult.Error -> this
        is DocVaultResult.Loading -> this
    }

inline fun <T> DocVaultResult<T>.onSuccess(crossinline onSuccess: (T) -> Unit): DocVaultResult<T> {
    if (this is DocVaultResult.Success) onSuccess(data)
    return this
}

inline fun <T> DocVaultResult<T>.onError(crossinline onError: (Throwable) -> Unit): DocVaultResult<T> {
    if (this is DocVaultResult.Error) onError(throwable)
    return this
}
