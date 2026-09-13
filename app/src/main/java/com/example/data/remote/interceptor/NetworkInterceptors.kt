package com.example.data.remote.interceptor

import android.util.Log
import com.example.BuildConfig
import com.example.data.remote.config.ApiConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Token provider abstraction for future JWT/OAuth authentication.
 * In P2B foundation, returns null by default until Phase 3 authentication.
 * Never hardcodes tokens or credentials.
 */
interface AuthTokenProvider {
    fun getAuthToken(): String?
}

class DefaultAuthTokenProvider : AuthTokenProvider {
    override fun getAuthToken(): String? = null
}

/**
 * Attaches Authorization header if token is present, and common headers (User-Agent, Version).
 */
class AuthInterceptor(
    private val tokenProvider: AuthTokenProvider = DefaultAuthTokenProvider()
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header(ApiConfig.HEADER_USER_AGENT, ApiConfig.USER_AGENT_VALUE)
            .header(ApiConfig.HEADER_CLIENT_VERSION, ApiConfig.CLIENT_VERSION_VALUE)

        val token = tokenProvider.getAuthToken()
        if (!token.isNullOrBlank()) {
            requestBuilder.header(ApiConfig.HEADER_AUTHORIZATION, "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}

/**
 * Security-compliant logging interceptor.
 * Guarantees:
 * - NO sensitive data (passwords, PINs, OTPs, Aadhaar, auth tokens, farmer personal docs) is logged.
 * - Completely disabled (Level.NONE) in release builds.
 */
object LoggingInterceptorFactory {

    private val SENSITIVE_KEYWORDS = listOf(
        "password",
        "token",
        "authorization",
        "pin",
        "otp",
        "aadhaar",
        "secret",
        "bank_account",
        "account_number"
    )

    fun create(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message ->
            var sanitizedMessage = message
            for (keyword in SENSITIVE_KEYWORDS) {
                if (sanitizedMessage.contains(keyword, ignoreCase = true)) {
                    sanitizedMessage = sanitizedMessage.replace(
                        Regex("(?i)(\"?$keyword\"?\\s*[:=]\\s*\"?)([^\",\\s]+)(\"?)"),
                        "$1[REDACTED]$3"
                    )
                }
            }
            if (BuildConfig.DEBUG) {
                Log.d("KisanSetu-Http", sanitizedMessage)
            }
        }

        logging.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        return logging
    }
}
