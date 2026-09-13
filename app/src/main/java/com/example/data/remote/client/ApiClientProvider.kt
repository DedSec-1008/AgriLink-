package com.example.data.remote.client

import com.example.data.remote.config.ApiConfig
import com.example.data.remote.interceptor.AuthInterceptor
import com.example.data.remote.interceptor.AuthTokenProvider
import com.example.data.remote.interceptor.DefaultAuthTokenProvider
import com.example.data.remote.interceptor.LoggingInterceptorFactory
import com.example.data.remote.service.BuyerApiService
import com.example.data.remote.service.MarketApiService
import com.example.data.remote.service.OfferApiService
import com.example.data.remote.service.ProduceLotApiService
import com.example.data.remote.service.TradeApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Production HTTP client and Retrofit instance provider for KisanSetu.
 * Configured with resilient timeouts, sanitized logging, and Moshi JSON adapter.
 */
object ApiClientProvider {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Builds a configured OkHttpClient with rural-network timeouts and interceptors.
     */
    fun createOkHttpClient(
        tokenProvider: AuthTokenProvider = DefaultAuthTokenProvider(),
        customInterceptor: okhttp3.Interceptor? = null
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(LoggingInterceptorFactory.create())

        if (customInterceptor != null) {
            builder.addInterceptor(customInterceptor)
        }

        return builder.build()
    }

    /**
     * Builds a Retrofit client targeting the configured [ApiConfig.baseUrl].
     */
    fun createRetrofit(
        okHttpClient: OkHttpClient = createOkHttpClient(),
        baseUrl: String = ApiConfig.baseUrl
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // Direct Service Factories
    fun createMarketApiService(retrofit: Retrofit = createRetrofit()): MarketApiService =
        retrofit.create(MarketApiService::class.java)

    fun createBuyerApiService(retrofit: Retrofit = createRetrofit()): BuyerApiService =
        retrofit.create(BuyerApiService::class.java)

    fun createProduceLotApiService(retrofit: Retrofit = createRetrofit()): ProduceLotApiService =
        retrofit.create(ProduceLotApiService::class.java)

    fun createOfferApiService(retrofit: Retrofit = createRetrofit()): OfferApiService =
        retrofit.create(OfferApiService::class.java)

    fun createTradeApiService(retrofit: Retrofit = createRetrofit()): TradeApiService =
        retrofit.create(TradeApiService::class.java)

    fun getMoshi(): Moshi = moshi
}
