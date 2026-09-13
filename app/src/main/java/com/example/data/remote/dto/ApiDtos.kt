package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Standard server-side error payload.
 */
@JsonClass(generateAdapter = true)
data class ApiErrorResponseDto(
    @field:Json(name = "code") val code: String? = null,
    @field:Json(name = "message") val message: String? = null,
    @field:Json(name = "details") val details: String? = null
)

// =========================================================================
// MARKET PRICE DTOS
// =========================================================================

@JsonClass(generateAdapter = true)
data class MarketPriceDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "crop_code") val cropCode: String,
    @field:Json(name = "crop_name") val cropName: String,
    @field:Json(name = "market_code") val marketCode: String,
    @field:Json(name = "market_name") val marketName: String,
    @field:Json(name = "modal_price_per_q") val modalPricePerQ: Int,
    @field:Json(name = "min_price_per_q") val minPricePerQ: Int = modalPricePerQ - 150,
    @field:Json(name = "max_price_per_q") val maxPricePerQ: Int = modalPricePerQ + 180,
    @field:Json(name = "price_change_text") val priceChangeText: String = "Stable vs yesterday",
    @field:Json(name = "is_positive_change") val isPositiveChange: Boolean = true,
    @field:Json(name = "data_source") val dataSource: String = "LIVE", // DEMO, CACHED, LIVE, STALE
    @field:Json(name = "last_updated_at") val lastUpdatedAt: String = ""
)

@JsonClass(generateAdapter = true)
data class MarketPriceListResponseDto(
    @field:Json(name = "data") val data: List<MarketPriceDto>,
    @field:Json(name = "total") val total: Int,
    @field:Json(name = "timestamp") val timestamp: String = ""
)

// =========================================================================
// BUYER DTOS
// =========================================================================

@JsonClass(generateAdapter = true)
data class BuyerDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "location") val location: String,
    @field:Json(name = "is_verified") val isVerified: Boolean = true,
    @field:Json(name = "commodity_code") val commodityCode: String = "soybean",
    @field:Json(name = "current_demand_min_q") val currentDemandMinQ: Int,
    @field:Json(name = "current_demand_max_q") val currentDemandMaxQ: Int,
    @field:Json(name = "quoted_price_per_q") val quotedPricePerQ: Int,
    @field:Json(name = "payment_speed_days") val paymentSpeedDays: Int = 2,
    @field:Json(name = "on_time_payment_pct") val onTimePaymentPct: Int = 98,
    @field:Json(name = "completed_transactions") val completedTransactions: Int = 120,
    @field:Json(name = "farmer_rating") val farmerRating: Double = 4.8,
    @field:Json(name = "distance_km") val distanceKm: Int = 28,
    @field:Json(name = "buys_commodities") val buysCommodities: List<String> = listOf("soybean", "wheat", "maize")
)

@JsonClass(generateAdapter = true)
data class BuyerListResponseDto(
    @field:Json(name = "data") val data: List<BuyerDto>,
    @field:Json(name = "total") val total: Int
)

// =========================================================================
// PRODUCE LOT DTOS
// =========================================================================

@JsonClass(generateAdapter = true)
data class ProduceLotDto(
    @field:Json(name = "lot_id") val lotId: String,
    @field:Json(name = "crop_code") val cropCode: String,
    @field:Json(name = "icon_emoji") val iconEmoji: String = "🌱",
    @field:Json(name = "quantity_quintals") val quantityQuintals: Int,
    @field:Json(name = "quality_grade") val qualityGrade: String = "GOOD",
    @field:Json(name = "status") val status: String = "PUBLISHED",
    @field:Json(name = "date_created") val dateCreated: String,
    @field:Json(name = "location") val location: String = "Nagpur, Maharashtra",
    @field:Json(name = "ready_timing") val readyTiming: String = "Ready now",
    @field:Json(name = "expected_price_per_q") val expectedPricePerQ: Int,
    @field:Json(name = "estimated_net_per_q") val estimatedNetPerQ: Int,
    @field:Json(name = "photos") val photos: List<String> = emptyList(),
    @field:Json(name = "farmer_id") val farmerId: String = "farmer_01"
)

@JsonClass(generateAdapter = true)
data class CreateLotRequestDto(
    @field:Json(name = "crop_code") val cropCode: String,
    @field:Json(name = "quantity_quintals") val quantityQuintals: Int,
    @field:Json(name = "quality_grade") val qualityGrade: String,
    @field:Json(name = "location") val location: String,
    @field:Json(name = "ready_timing") val readyTiming: String,
    @field:Json(name = "expected_price_per_q") val expectedPricePerQ: Int,
    @field:Json(name = "estimated_net_per_q") val estimatedNetPerQ: Int,
    @field:Json(name = "idempotency_key") val idempotencyKey: String
)

@JsonClass(generateAdapter = true)
data class LotListResponseDto(
    @field:Json(name = "data") val data: List<ProduceLotDto>,
    @field:Json(name = "total") val total: Int
)

// =========================================================================
// OFFER DTOS
// =========================================================================

@JsonClass(generateAdapter = true)
data class OfferDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "lot_id") val lotId: String,
    @field:Json(name = "buyer_id") val buyerId: String,
    @field:Json(name = "buyer_name") val buyerName: String,
    @field:Json(name = "is_verified_buyer") val isVerifiedBuyer: Boolean = true,
    @field:Json(name = "farmer_rating") val farmerRating: Double = 4.8,
    @field:Json(name = "price_per_quintal") val pricePerQuintal: Int,
    @field:Json(name = "quantity_quintals") val quantityQuintals: Int,
    @field:Json(name = "transport_expense_per_q") val transportExpensePerQ: Int = 120,
    @field:Json(name = "other_expense_per_q") val otherExpensePerQ: Int = 30,
    @field:Json(name = "status") val status: String = "PENDING",
    @field:Json(name = "created_at") val createdAt: String = "Today",
    @field:Json(name = "expires_at") val expiresAt: String = "Valid for 24 hours",
    @field:Json(name = "distance_km") val distanceKm: Int = 28,
    @field:Json(name = "is_best_offer") val isBestOffer: Boolean = false
)

@JsonClass(generateAdapter = true)
data class OfferListResponseDto(
    @field:Json(name = "data") val data: List<OfferDto>,
    @field:Json(name = "lot_id") val lotId: String
)

@JsonClass(generateAdapter = true)
data class AcceptOfferRequestDto(
    @field:Json(name = "offer_id") val offerId: String,
    @field:Json(name = "idempotency_key") val idempotencyKey: String
)

// =========================================================================
// TRANSACTION & LOGISTICS DTOS
// =========================================================================

@JsonClass(generateAdapter = true)
data class AgriTransactionDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "lot_id") val lotId: String,
    @field:Json(name = "offer_id") val offerId: String,
    @field:Json(name = "buyer_id") val buyerId: String,
    @field:Json(name = "buyer_name") val buyerName: String,
    @field:Json(name = "crop_code") val cropCode: String,
    @field:Json(name = "quantity_quintals") val quantityQuintals: Int,
    @field:Json(name = "agreed_price_per_q") val agreedPricePerQ: Int,
    @field:Json(name = "status") val status: String = "OFFER_ACCEPTED",
    @field:Json(name = "payment_status") val paymentStatus: String = "PENDING",
    @field:Json(name = "created_at") val createdAt: String = "Today"
)

@JsonClass(generateAdapter = true)
data class TransactionListResponseDto(
    @field:Json(name = "data") val data: List<AgriTransactionDto>
)

@JsonClass(generateAdapter = true)
data class UpdateTransactionStatusRequestDto(
    @field:Json(name = "transaction_id") val transactionId: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "idempotency_key") val idempotencyKey: String
)

@JsonClass(generateAdapter = true)
data class TransportOptionDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "vehicle_type") val vehicleType: String,
    @field:Json(name = "total_cost") val totalCost: Int,
    @field:Json(name = "cost_per_q") val costPerQ: Int,
    @field:Json(name = "capacity_q") val capacityQ: Int = 100
)

@JsonClass(generateAdapter = true)
data class BookTransportRequestDto(
    @field:Json(name = "transaction_id") val transactionId: String,
    @field:Json(name = "transporter_id") val transporterId: String,
    @field:Json(name = "idempotency_key") val idempotencyKey: String
)

@JsonClass(generateAdapter = true)
data class LogisticsBookingDto(
    @field:Json(name = "booking_id") val bookingId: String,
    @field:Json(name = "transaction_id") val transactionId: String,
    @field:Json(name = "transporter_name") val transporterName: String,
    @field:Json(name = "vehicle_type") val vehicleType: String,
    @field:Json(name = "pickup_location") val pickupLocation: String,
    @field:Json(name = "delivery_location") val deliveryLocation: String,
    @field:Json(name = "total_cost") val totalCost: Int,
    @field:Json(name = "is_confirmed") val isConfirmed: Boolean = true
)

// =========================================================================
// PAYMENT & GRIEVANCE DTOS
// =========================================================================

@JsonClass(generateAdapter = true)
data class PaymentRecordDto(
    @field:Json(name = "transaction_id") val transactionId: String,
    @field:Json(name = "amount") val amount: Int,
    @field:Json(name = "status") val status: String = "PENDING",
    @field:Json(name = "method") val method: String = "Direct Bank Transfer",
    @field:Json(name = "reference_number") val referenceNumber: String? = null,
    @field:Json(name = "payment_date") val paymentDate: String = "Today"
)

@JsonClass(generateAdapter = true)
data class GrievanceDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "transaction_id") val transactionId: String,
    @field:Json(name = "issue_type") val issueType: String,
    @field:Json(name = "details") val details: String,
    @field:Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class SubmitGrievanceRequestDto(
    @field:Json(name = "transaction_id") val transactionId: String,
    @field:Json(name = "issue_type") val issueType: String,
    @field:Json(name = "details") val details: String,
    @field:Json(name = "idempotency_key") val idempotencyKey: String
)
