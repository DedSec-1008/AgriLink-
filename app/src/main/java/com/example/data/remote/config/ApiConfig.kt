package com.example.data.remote.config

/**
 * Supported API environments for KisanSetu.
 * Explicitly separates development, staging, and production gateways.
 */
enum class ApiEnvironment(
    val baseUrl: String,
    val environmentName: String,
    val requiresHttps: Boolean
) {
    DEVELOPMENT(
        baseUrl = "https://dev-api.kisansetu.in/v1/",
        environmentName = "Development",
        requiresHttps = true
    ),
    STAGING(
        baseUrl = "https://staging-api.kisansetu.in/v1/",
        environmentName = "Staging",
        requiresHttps = true
    ),
    PRODUCTION(
        baseUrl = "https://api.kisansetu.in/v1/",
        environmentName = "Production",
        requiresHttps = true
    )
}

/**
 * Centralized network environment and timeout configuration.
 * Configured specifically for Indian rural agricultural connectivity realities:
 * - 15s connect timeout for slow cellular/tower handshake
 * - 20s read timeout for market payload streaming
 * - 20s write timeout for produce lot creation & photo uploads
 */
object ApiConfig {
    // Default environment for testing and active development
    @Volatile
    var currentEnvironment: ApiEnvironment = ApiEnvironment.DEVELOPMENT

    val baseUrl: String
        get() = currentEnvironment.baseUrl

    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 20L
    const val WRITE_TIMEOUT_SECONDS = 20L

    const val HEADER_USER_AGENT = "User-Agent"
    const val USER_AGENT_VALUE = "KisanSetu-Android/1.0"

    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_IDEMPOTENCY_KEY = "X-Idempotency-Key"
    const val HEADER_CLIENT_VERSION = "X-App-Version"
    const val CLIENT_VERSION_VALUE = "1.0.0"

    /**
     * Resets environment to default.
     */
    fun resetToDefault() {
        currentEnvironment = ApiEnvironment.DEVELOPMENT
    }
}
