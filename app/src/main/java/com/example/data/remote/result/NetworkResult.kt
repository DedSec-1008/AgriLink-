package com.example.data.remote.result

import com.example.data.remote.error.NetworkError

/**
 * Standard Result wrapper for remote network and repository operations.
 * Supports offline-first cache awareness: both [Loading] and [Error] states
 * can hold existing [cachedData] from Room so UI can render stale data smoothly.
 */
sealed class NetworkResult<out T> {

    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Error<out T>(
        val error: NetworkError,
        val cachedData: T? = null
    ) : NetworkResult<T>()

    data class Loading<out T>(val cachedData: T? = null) : NetworkResult<T>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> cachedData
        is Loading -> cachedData
    }

    inline fun <R> map(transform: (T) -> R): NetworkResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(error, cachedData?.let(transform))
        is Loading -> Loading(cachedData?.let(transform))
    }

    inline fun onSuccess(action: (T) -> Unit): NetworkResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (NetworkError, T?) -> Unit): NetworkResult<T> {
        if (this is Error) action(error, cachedData)
        return this
    }
}
