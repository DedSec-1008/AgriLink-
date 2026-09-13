package com.example.data.remote.retry

import com.example.data.remote.error.NetworkError
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Idempotency & Safe Retry Policy for KisanSetu.
 *
 * Strict Rules:
 * 1. GET / read requests (market prices, buyer lists, offers query) are idempotent and can be safely retried.
 * 2. State-changing mutation requests (accept offer, create lot, book transport, submit grievance)
 *    MUST NOT be blindly retried automatically without explicit client idempotency tokens.
 * 3. Client errors (400, 401, 403, 404, 409) must NEVER be retried.
 */
object RetryPolicy {

    const val MAX_READ_RETRIES = 2
    const val INITIAL_BACKOFF_MS = 1000L
    const val BACKOFF_MULTIPLIER = 2.0

    /**
     * Generates a unique client-side idempotency key for state-changing network requests.
     */
    fun generateIdempotencyKey(): String = UUID.randomUUID().toString()

    /**
     * Evaluates whether a failed operation is safe to automatically retry.
     */
    fun isSafeToRetry(httpMethod: String, error: NetworkError): Boolean {
        // Only GET requests are automatically retried
        if (!httpMethod.equals("GET", ignoreCase = true)) {
            return false
        }

        return when (error) {
            is NetworkError.Timeout -> true
            is NetworkError.ConnectionFailed -> true
            is NetworkError.HttpError -> {
                // Only retry transient 5xx gateway errors (502, 503, 504)
                error.code in listOf(502, 503, 504)
            }
            is NetworkError.NoInternet -> false // Device offline; immediate retry will just fail
            is NetworkError.MalformedResponse -> false // Payload broken; retry won't fix
            is NetworkError.Unexpected -> false
        }
    }

    /**
     * Executes an idempotent block with exponential backoff if eligible.
     */
    suspend fun <T> executeWithRetry(
        httpMethod: String = "GET",
        maxRetries: Int = MAX_READ_RETRIES,
        block: suspend (attempt: Int) -> T
    ): T {
        var currentAttempt = 0
        var currentDelay = INITIAL_BACKOFF_MS

        while (true) {
            try {
                return block(currentAttempt)
            } catch (throwable: Throwable) {
                val mappedError = com.example.data.remote.error.NetworkExceptionMapper.map(throwable)
                if (currentAttempt < maxRetries && isSafeToRetry(httpMethod, mappedError)) {
                    currentAttempt++
                    delay(currentDelay)
                    currentDelay = (currentDelay * BACKOFF_MULTIPLIER).toLong()
                } else {
                    throw throwable
                }
            }
        }
    }
}
