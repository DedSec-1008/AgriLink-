package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.Offer
import com.example.model.OfferStatus

/**
 * Local Room entity representing a buyer's offer for a specific crop lot.
 * Indexed on lotId, buyerId, and status for instant filtering.
 */
@Entity(
    tableName = "offers",
    indices = [
        Index(value = ["lotId"]),
        Index(value = ["buyerId"]),
        Index(value = ["status"])
    ]
)
data class OfferEntity(
    @PrimaryKey val id: String,
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
    val reliabilityTextRes: Int,
    val isBestOffer: Boolean = false,
    val whyBetterReasons: List<Int> = emptyList(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): Offer = Offer(
        id = id,
        lotId = lotId,
        buyerId = buyerId,
        buyerNameRes = buyerNameRes,
        isVerifiedBuyer = isVerifiedBuyer,
        farmerRating = farmerRating,
        pricePerQuintal = pricePerQuintal,
        quantityQuintals = quantityQuintals,
        quotedTotalAmount = quotedTotalAmount,
        transportExpensePerQ = transportExpensePerQ,
        otherExpensePerQ = otherExpensePerQ,
        estimatedNetAmount = estimatedNetAmount,
        paymentTermsRes = paymentTermsRes,
        deliveryRequirementsRes = deliveryRequirementsRes,
        qualityRequirementsRes = qualityRequirementsRes,
        status = status,
        createdAt = createdAt,
        expiresAt = expiresAt,
        distanceKm = distanceKm,
        reliabilityTextRes = reliabilityTextRes,
        isBestOffer = isBestOffer,
        whyBetterReasons = whyBetterReasons
    )

    companion object {
        fun fromDomain(offer: Offer, syncStatus: SyncStatus = SyncStatus.SYNCED): OfferEntity =
            OfferEntity(
                id = offer.id,
                lotId = offer.lotId,
                buyerId = offer.buyerId,
                buyerNameRes = offer.buyerNameRes,
                isVerifiedBuyer = offer.isVerifiedBuyer,
                farmerRating = offer.farmerRating,
                pricePerQuintal = offer.pricePerQuintal,
                quantityQuintals = offer.quantityQuintals,
                quotedTotalAmount = offer.quotedTotalAmount,
                transportExpensePerQ = offer.transportExpensePerQ,
                otherExpensePerQ = offer.otherExpensePerQ,
                estimatedNetAmount = offer.estimatedNetAmount,
                paymentTermsRes = offer.paymentTermsRes,
                deliveryRequirementsRes = offer.deliveryRequirementsRes,
                qualityRequirementsRes = offer.qualityRequirementsRes,
                status = offer.status,
                createdAt = offer.createdAt,
                expiresAt = offer.expiresAt,
                distanceKm = offer.distanceKm,
                reliabilityTextRes = offer.reliabilityTextRes,
                isBestOffer = offer.isBestOffer,
                whyBetterReasons = offer.whyBetterReasons,
                syncStatus = syncStatus
            )
    }
}
