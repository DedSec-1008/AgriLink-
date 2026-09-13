package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.R
import com.example.model.Buyer

/**
 * Local Room entity representing verified agricultural produce buyers.
 */
@Entity(tableName = "buyers")
data class BuyerEntity(
    @PrimaryKey val id: String,
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
    val buysCommodities: List<Int> = emptyList()
) {
    fun toDomain(): Buyer = Buyer(
        id = id,
        nameRes = nameRes,
        locationRes = locationRes,
        isVerified = isVerified,
        commodityRes = commodityRes,
        currentDemandMinQ = currentDemandMinQ,
        currentDemandMaxQ = currentDemandMaxQ,
        requiredQualityRes = requiredQualityRes,
        quotedPricePerQ = quotedPricePerQ,
        paymentDaysDescriptionRes = paymentDaysDescriptionRes,
        onTimePaymentPct = onTimePaymentPct,
        completedTransactions = completedTransactions,
        farmerRating = farmerRating,
        neededTimelineRes = neededTimelineRes,
        disputeRatePct = disputeRatePct,
        averagePaymentDays = averagePaymentDays,
        paymentTermsRes = paymentTermsRes,
        deliveryTermsRes = deliveryTermsRes,
        qualityRequirementsRes = qualityRequirementsRes,
        distanceKm = distanceKm,
        reliabilityTextRes = reliabilityTextRes,
        buysCommodities = buysCommodities
    )

    companion object {
        fun fromDomain(b: Buyer): BuyerEntity = BuyerEntity(
            id = b.id,
            nameRes = b.nameRes,
            locationRes = b.locationRes,
            isVerified = b.isVerified,
            commodityRes = b.commodityRes,
            currentDemandMinQ = b.currentDemandMinQ,
            currentDemandMaxQ = b.currentDemandMaxQ,
            requiredQualityRes = b.requiredQualityRes,
            quotedPricePerQ = b.quotedPricePerQ,
            paymentDaysDescriptionRes = b.paymentDaysDescriptionRes,
            onTimePaymentPct = b.onTimePaymentPct,
            completedTransactions = b.completedTransactions,
            farmerRating = b.farmerRating,
            neededTimelineRes = b.neededTimelineRes,
            disputeRatePct = b.disputeRatePct,
            averagePaymentDays = b.averagePaymentDays,
            paymentTermsRes = b.paymentTermsRes,
            deliveryTermsRes = b.deliveryTermsRes,
            qualityRequirementsRes = b.qualityRequirementsRes,
            distanceKm = b.distanceKm,
            reliabilityTextRes = b.reliabilityTextRes,
            buysCommodities = b.buysCommodities
        )
    }
}
