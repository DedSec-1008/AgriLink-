package com.example.data.remote.error

import androidx.annotation.StringRes
import com.example.R
import com.example.data.remote.dto.ApiErrorResponseDto
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

/**
 * Standardized application-level network error model.
 * Bridges low-level socket/HTTP exceptions to farmer-safe, localized user strings.
 * Raw exceptions remain strictly in [diagnosticMessage] for logging/telemetry.
 */
sealed class NetworkError(
    @StringRes val userMessageRes: Int,
    val diagnosticMessage: String,
    val cause: Throwable? = null
) {
    /**
     * Device has no active cellular or Wi-Fi network route.
     */
    data class NoInternet(
        val devMessage: String = "No internet connectivity",
        val rootCause: Throwable? = null
    ) : NetworkError(
        userMessageRes = R.string.error_network_no_internet,
        diagnosticMessage = devMessage,
        cause = rootCause
    )

    /**
     * TCP handshake or read/write deadline exceeded (e.g. 15s/20s timeout on 2G edge).
     */
    data class Timeout(
        val devMessage: String = "Request timed out",
        val rootCause: Throwable? = null
    ) : NetworkError(
        userMessageRes = R.string.error_network_timeout,
        diagnosticMessage = devMessage,
        cause = rootCause
    )

    /**
     * Low-level socket failure, server reset, or SSL handshake error.
     */
    data class ConnectionFailed(
        val devMessage: String = "Failed to establish server connection",
        val rootCause: Throwable? = null
    ) : NetworkError(
        userMessageRes = R.string.error_network_connection_failed,
        diagnosticMessage = devMessage,
        cause = rootCause
    )

    /**
     * Standard HTTP response codes with specialized farmer-facing translations.
     */
    data class HttpError(
        val code: Int,
        val errorBody: String? = null,
        val parsedError: ApiErrorResponseDto? = null,
        val devMessage: String = "HTTP $code"
    ) : NetworkError(
        userMessageRes = when (code) {
            400 -> R.string.error_network_generic
            401 -> R.string.error_network_unauthorized
            403 -> R.string.error_network_forbidden
            404 -> R.string.error_network_not_found
            409 -> R.string.error_network_conflict
            429 -> R.string.error_network_rate_limit
            in 500..599 -> R.string.error_network_server_error
            else -> R.string.error_network_generic
        },
        diagnosticMessage = "HTTP error $code: $devMessage (body: $errorBody)"
    ) {
        val isClientError: Boolean get() = code in 400..499
        val isServerError: Boolean get() = code in 500..599
        val isUnauthorized: Boolean get() = code == 401
        val isForbidden: Boolean get() = code == 403
        val isNotFound: Boolean get() = code == 404
        val isConflict: Boolean get() = code == 409
        val isRateLimited: Boolean get() = code == 429
    }

    /**
     * Payload was truncated, corrupted, or failed Moshi schema validation.
     */
    data class MalformedResponse(
        val devMessage: String = "JSON deserialization or schema mismatch",
        val rootCause: Throwable? = null
    ) : NetworkError(
        userMessageRes = R.string.error_network_malformed,
        diagnosticMessage = devMessage,
        cause = rootCause
    )

    /**
     * Unexpected runtime exception or uncaught error.
     */
    data class Unexpected(
        val rootCause: Throwable? = null,
        val devMessage: String = rootCause?.localizedMessage ?: "Unexpected error"
    ) : NetworkError(
        userMessageRes = R.string.error_network_generic,
        diagnosticMessage = devMessage,
        cause = rootCause
    )
}

/**
 * Maps raw Java/Kotlin runtime exceptions into clean [NetworkError] models.
 */
object NetworkExceptionMapper {

    fun map(throwable: Throwable): NetworkError {
        return when (throwable) {
            is UnknownHostException -> NetworkError.NoInternet(
                devMessage = "Host resolution failed: ${throwable.message}",
                rootCause = throwable
            )
            is SocketTimeoutException -> NetworkError.Timeout(
                devMessage = "Socket timeout: ${throwable.message}",
                rootCause = throwable
            )
            is InterruptedIOException -> NetworkError.Timeout(
                devMessage = "IO interrupted / timeout: ${throwable.message}",
                rootCause = throwable
            )
            is ConnectException -> NetworkError.ConnectionFailed(
                devMessage = "Connection refused or unreachable: ${throwable.message}",
                rootCause = throwable
            )
            is HttpException -> {
                val code = throwable.code()
                val body = try {
                    throwable.response()?.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                NetworkError.HttpError(
                    code = code,
                    errorBody = body,
                    devMessage = throwable.message()
                )
            }
            is com.squareup.moshi.JsonDataException -> NetworkError.MalformedResponse(
                devMessage = "Moshi JSON schema error: ${throwable.message}",
                rootCause = throwable
            )
            is com.squareup.moshi.JsonEncodingException -> NetworkError.MalformedResponse(
                devMessage = "Moshi JSON encoding error: ${throwable.message}",
                rootCause = throwable
            )
            is IOException -> NetworkError.ConnectionFailed(
                devMessage = "General IO network error: ${throwable.message}",
                rootCause = throwable
            )
            else -> NetworkError.Unexpected(
                rootCause = throwable,
                devMessage = throwable.localizedMessage ?: "Unexpected runtime exception"
            )
        }
    }
}
