package com.example.model

import com.example.R

data class ProduceItem(
    val cropNameRes: Int,
    val iconEmoji: String,
    val quantityQuintals: Int,
    val qualityRes: Int
)

data class MarketPriceInfo(
    val id: String,
    val cropNameRes: Int,
    val marketNameRes: Int,
    val pricePerQuintal: Int,
    val priceChangeTextRes: Int,
    val isPositiveChange: Boolean
)

enum class DestinationType {
    BUYER,
    MARKET
}

enum class RecommendationCalculationState {
    IDLE,
    CALCULATING,
    SUCCESS,
    ERROR
}

data class SellingOpportunity(
    val id: String = "opp_1",
    val buyerNameRes: Int,
    val buyerTypeRes: Int = R.string.buyer_type_verified_direct,
    val cropNameRes: Int,
    val quantityQuintals: Int,
    val quotedPricePerQ: Int,
    val transportExpensePerQ: Int,
    val otherExpensePerQ: Int = 30,
    val handlingCostPerQ: Int = 20,
    val storageCostPerQ: Int = 0,
    val transactionCostPerQ: Int = 10,
    val netRealizationPerQ: Int,
    val estimatedTotalAmount: Int,
    val statusTextRes: Int,
    val recommendationStrength: String = "STRONG", // "STRONG", "GOOD", "MODERATE"
    val isTopRecommendation: Boolean = false,
    val distanceKm: Int = 18,
    val paymentReliabilityRes: Int = R.string.payment_same_day,
    val reasonsRes: List<Int> = emptyList(),
    val cautionsRes: List<Int> = emptyList(),
    val quantityMatch: Boolean = true,
    val qualityMatch: Boolean = true,
    val isVerifiedBuyer: Boolean = true,
    val destinationType: DestinationType = DestinationType.BUYER,
    val destinationName: String = "",
    val matchQualityRes: Int = R.string.status_strong_rec,
    val paymentDaysEstimate: Int = 2,
    val completedTransactionsCount: Int = 128
) {
    val totalExpensePerQ: Int
        get() = transportExpensePerQ + otherExpensePerQ

    val estimatedNetPricePerQ: Int
        get() = netRealizationPerQ

    val isMarket: Boolean
        get() = destinationType == DestinationType.MARKET
}

enum class LotStatus(val labelRes: Int) {
    DRAFT(R.string.lot_status_draft),
    PUBLISHED(R.string.lot_status_published),
    OFFER_ACCEPTED(R.string.status_offer_accepted)
}

data class ProduceLot(
    val lotId: String,
    val cropNameRes: Int,
    val iconEmoji: String,
    val quantityQuintals: Int,
    val qualityRes: Int,
    val statusRes: Int,
    val dateCreated: String,
    val buyerNameRes: Int? = null,
    val location: String = "Nagpur, Maharashtra",
    val readyTiming: String = "Ready now",
    val expectedPricePerQ: Int = 4850,
    val estimatedNetPerQ: Int = 4700,
    val photos: List<String> = emptyList(),
    val farmerId: String = "farmer_nagpur_01",
    val lotStatus: LotStatus = LotStatus.PUBLISHED
) {
    val estimatedTotalAmount: Int
        get() = estimatedNetPerQ * quantityQuintals
}

data class Buyer(
    val id: String,
    val nameRes: Int,
    val locationRes: Int,
    val isVerified: Boolean = true,
    val commodityRes: Int,
    val currentDemandMinQ: Int,
    val currentDemandMaxQ: Int,
    val requiredQualityRes: Int,
    val quotedPricePerQ: Int,
    val paymentDaysDescriptionRes: Int,
    val onTimePaymentPct: Int,
    val completedTransactions: Int,
    val farmerRating: Double,
    val neededTimelineRes: Int,
    val disputeRatePct: Double = 0.8,
    val averagePaymentDays: Int = 2,
    val paymentTermsRes: Int = R.string.buyer_payment_terms_direct,
    val deliveryTermsRes: Int = R.string.buyer_delivery_center_or_farmgate,
    val qualityRequirementsRes: Int = R.string.buyer_quality_requirement,
    val distanceKm: Int = 28,
    val reliabilityTextRes: Int = R.string.reliability_very_reliable,
    val buysCommodities: List<Int> = listOf(R.string.crop_soybean, R.string.crop_wheat, R.string.crop_maize)
)

data class BuyerRequirement(
    val id: String,
    val buyerId: String,
    val cropNameRes: Int,
    val quantityMin: Int,
    val quantityMax: Int,
    val qualityRes: Int,
    val requiredByRes: Int,
    val status: String = "ACTIVE"
)

enum class OfferStatus(val labelRes: Int) {
    PENDING(R.string.offer_status_pending),
    ACCEPTED(R.string.offer_status_accepted),
    REJECTED(R.string.offer_status_rejected),
    EXPIRED(R.string.offer_status_expired),
    CANCELLED(R.string.offer_status_cancelled),
    CLOSED(R.string.offer_status_closed)
}

data class Offer(
    val id: String,
    val lotId: String,
    val buyerId: String,
    val buyerNameRes: Int,
    val isVerifiedBuyer: Boolean,
    val farmerRating: Double,
    val pricePerQuintal: Int,
    val quantityQuintals: Int,
    val quotedTotalAmount: Int,
    val transportExpensePerQ: Int,
    val otherExpensePerQ: Int,
    val estimatedNetAmount: Int,
    val paymentTermsRes: Int,
    val deliveryRequirementsRes: Int,
    val qualityRequirementsRes: Int,
    val status: OfferStatus = OfferStatus.PENDING,
    val createdAt: String = "Today",
    val expiresAt: String = "Valid for 24 hours",
    val distanceKm: Int = 28,
    val reliabilityTextRes: Int = R.string.reliability_very_reliable,
    val isBestOffer: Boolean = false,
    val whyBetterReasons: List<Int> = emptyList()
) {
    val estimatedNetPricePerQ: Int
        get() = pricePerQuintal - transportExpensePerQ - otherExpensePerQ

    val totalExpensesPerQ: Int
        get() = transportExpensePerQ + otherExpensePerQ
}

enum class TransactionStatus(val labelRes: Int) {
    OFFER_ACCEPTED(R.string.tx_status_offer_accepted),
    LOGISTICS_BOOKED(R.string.tx_status_logistics_booked),
    DISPATCHED(R.string.tx_status_dispatched),
    DELIVERED(R.string.tx_status_delivered),
    PAYMENT_INITIATED(R.string.tx_status_payment_initiated),
    PAYMENT_RECEIVED(R.string.tx_status_payment_received),
    COMPLETED(R.string.tx_status_completed),
    PAYMENT_DELAYED(R.string.tx_status_payment_delayed),
    CANCELLED(R.string.tx_status_cancelled),
    DISPUTED(R.string.tx_status_disputed)
}

enum class PaymentStatus(val labelRes: Int) {
    PENDING(R.string.payment_status_pending),
    INITIATED(R.string.payment_status_initiated),
    RECEIVED(R.string.payment_status_received),
    DELAYED(R.string.payment_status_delayed)
}

data class TransporterOption(
    val id: String,
    val name: String,
    val vehicleTypeRes: Int,
    val isVerified: Boolean = true,
    val rating: Double = 4.6,
    val estimatedPickupTimeRes: Int = R.string.estimated_pickup_time,
    val distanceKm: Int = 42,
    val totalCost: Int = 6000,
    val costPerQuintal: Int = 120,
    val pickupLocationRes: Int = R.string.loc_nagpur,
    val deliveryLocation: String = "ABC Foods"
)

data class TransportBooking(
    val bookingId: String,
    val transactionId: String,
    val transporterName: String,
    val vehicleTypeRes: Int,
    val pickupLocation: String,
    val deliveryLocation: String,
    val pickupTime: String,
    val distanceKm: Int = 42,
    val totalCost: Int = 6000,
    val costPerQ: Int = 120,
    val isConfirmed: Boolean = true
)

data class PaymentDetails(
    val transactionId: String,
    val buyerNameRes: Int,
    val cropNameRes: Int,
    val quantityQuintals: Int,
    val agreedPricePerQ: Int,
    val grossProduceValue: Int,
    val transportCost: Int,
    val otherCosts: Int,
    val estimatedNetAmount: Int,
    val status: PaymentStatus,
    val paymentDate: String = "Today",
    val paymentMethod: String = "Direct Bank Transfer (RTGS/NEFT)"
)

data class GrievanceIssue(
    val id: String,
    val transactionId: String,
    val issueTypeRes: Int,
    val details: String = "",
    val createdAt: String = "Today"
)

data class AgriTransaction(
    val id: String,
    val lotId: String,
    val offerId: String,
    val buyerId: String,
    val buyerNameRes: Int,
    val cropNameRes: Int,
    val iconEmoji: String,
    val quantityQuintals: Int,
    val agreedPricePerQ: Int,
    val grossProduceValue: Int = quantityQuintals * agreedPricePerQ,
    val transportDeduction: Int = quantityQuintals * 120,
    val otherDeductions: Int = quantityQuintals * 30,
    val estimatedNetAmount: Int = (quantityQuintals * agreedPricePerQ) - (quantityQuintals * 120) - (quantityQuintals * 30),
    val status: TransactionStatus = TransactionStatus.OFFER_ACCEPTED,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val transporterBooking: TransportBooking? = null,
    val createdAt: String = "Today",
    val buyerRating: Int? = null,
    val buyerFeedback: String? = null
)

data class CropOption(
    val id: String,
    val nameRes: Int,
    val emoji: String,
    val typicalPrice: Int
)

data class HelpCategory(
    val id: String,
    val titleRes: Int,
    val iconEmoji: String
)
