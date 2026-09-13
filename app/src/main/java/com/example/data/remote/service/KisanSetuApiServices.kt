package com.example.data.remote.service

import com.example.data.remote.config.ApiConfig
import com.example.data.remote.dto.AcceptOfferRequestDto
import com.example.data.remote.dto.AgriTransactionDto
import com.example.data.remote.dto.BookTransportRequestDto
import com.example.data.remote.dto.BuyerDto
import com.example.data.remote.dto.BuyerListResponseDto
import com.example.data.remote.dto.CreateLotRequestDto
import com.example.data.remote.dto.GrievanceDto
import com.example.data.remote.dto.LogisticsBookingDto
import com.example.data.remote.dto.LotListResponseDto
import com.example.data.remote.dto.MarketPriceDto
import com.example.data.remote.dto.MarketPriceListResponseDto
import com.example.data.remote.dto.OfferListResponseDto
import com.example.data.remote.dto.ProduceLotDto
import com.example.data.remote.dto.SubmitGrievanceRequestDto
import com.example.data.remote.dto.TransactionListResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for remote agricultural market prices (e.g. APMC / Mandi rates).
 */
interface MarketApiService {
    @GET("market/prices")
    suspend fun getMarketPrices(
        @Query("commodity") commodity: String? = null,
        @Query("location") location: String? = null
    ): MarketPriceListResponseDto

    @GET("market/prices/{id}")
    suspend fun getMarketPriceById(@Path("id") id: String): MarketPriceDto
}

/**
 * Retrofit interface for verified institutional buyers and aggregators.
 */
interface BuyerApiService {
    @GET("buyers")
    suspend fun getBuyers(
        @Query("commodity") commodity: String? = null
    ): BuyerListResponseDto

    @GET("buyers/{buyerId}")
    suspend fun getBuyerDetails(@Path("buyerId") buyerId: String): BuyerDto
}

/**
 * Retrofit interface for farmer produce lot listings.
 */
interface ProduceLotApiService {
    @GET("lots")
    suspend fun getLots(): LotListResponseDto

    @GET("lots/{lotId}")
    suspend fun getLotById(@Path("lotId") lotId: String): ProduceLotDto

    @POST("lots")
    suspend fun createLot(
        @Header(ApiConfig.HEADER_IDEMPOTENCY_KEY) idempotencyKey: String,
        @Body request: CreateLotRequestDto
    ): ProduceLotDto
}

/**
 * Retrofit interface for buyer bids and offers.
 */
interface OfferApiService {
    @GET("lots/{lotId}/offers")
    suspend fun getOffersForLot(@Path("lotId") lotId: String): OfferListResponseDto

    @POST("offers/{offerId}/accept")
    suspend fun acceptOffer(
        @Path("offerId") offerId: String,
        @Header(ApiConfig.HEADER_IDEMPOTENCY_KEY) idempotencyKey: String,
        @Body request: AcceptOfferRequestDto
    ): AgriTransactionDto
}

/**
 * Retrofit interface for end-to-end post-trade lifecycle: transactions, transport, payments, grievances.
 */
interface TradeApiService {
    @GET("transactions")
    suspend fun getTransactions(): TransactionListResponseDto

    @GET("transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): AgriTransactionDto

    @POST("logistics/book")
    suspend fun bookTransport(
        @Header(ApiConfig.HEADER_IDEMPOTENCY_KEY) idempotencyKey: String,
        @Body request: BookTransportRequestDto
    ): LogisticsBookingDto

    @POST("grievances")
    suspend fun submitGrievance(
        @Header(ApiConfig.HEADER_IDEMPOTENCY_KEY) idempotencyKey: String,
        @Body request: SubmitGrievanceRequestDto
    ): GrievanceDto
}
