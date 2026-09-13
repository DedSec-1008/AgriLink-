package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.AgriTransaction
import com.example.model.PaymentStatus
import com.example.model.TransactionStatus
import com.example.model.TransportBooking

/**
 * Local Room entity representing a trade transaction between a farmer and a buyer.
 * Tracks the complete lifecycle from offer acceptance through logistics, delivery,
 * and escrow payout.
 */
@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["lotId"]),
        Index(value = ["offerId"]),
        Index(value = ["buyerId"]),
        Index(value = ["status"])
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val lotId: String,
    val offerId: String,
    val buyerId: String,
    val buyerNameRes: Int,
    val cropNameRes: Int,
    val iconEmoji: String,
    val quantityQuintals: Int,
    val agreedPricePerQ: Int,
    val grossProduceValue: Int,
    val transportDeduction: Int,
    val otherDeductions: Int,
    val estimatedNetAmount: Int,
    val status: TransactionStatus = TransactionStatus.OFFER_ACCEPTED,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val createdAt: String = "Today",
    val buyerRating: Int? = null,
    val buyerFeedback: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(booking: TransportBooking? = null): AgriTransaction = AgriTransaction(
        id = id,
        lotId = lotId,
        offerId = offerId,
        buyerId = buyerId,
        buyerNameRes = buyerNameRes,
        cropNameRes = cropNameRes,
        iconEmoji = iconEmoji,
        quantityQuintals = quantityQuintals,
        agreedPricePerQ = agreedPricePerQ,
        grossProduceValue = grossProduceValue,
        transportDeduction = transportDeduction,
        otherDeductions = otherDeductions,
        estimatedNetAmount = estimatedNetAmount,
        status = status,
        paymentStatus = paymentStatus,
        transporterBooking = booking,
        createdAt = createdAt,
        buyerRating = buyerRating,
        buyerFeedback = buyerFeedback
    )

    companion object {
        fun fromDomain(tx: AgriTransaction, syncStatus: SyncStatus = SyncStatus.SYNCED): TransactionEntity =
            TransactionEntity(
                id = tx.id,
                lotId = tx.lotId,
                offerId = tx.offerId,
                buyerId = tx.buyerId,
                buyerNameRes = tx.buyerNameRes,
                cropNameRes = tx.cropNameRes,
                iconEmoji = tx.iconEmoji,
                quantityQuintals = tx.quantityQuintals,
                agreedPricePerQ = tx.agreedPricePerQ,
                grossProduceValue = tx.grossProduceValue,
                transportDeduction = tx.transportDeduction,
                otherDeductions = tx.otherDeductions,
                estimatedNetAmount = tx.estimatedNetAmount,
                status = tx.status,
                paymentStatus = tx.paymentStatus,
                createdAt = tx.createdAt,
                buyerRating = tx.buyerRating,
                buyerFeedback = tx.buyerFeedback,
                syncStatus = syncStatus
            )
    }
}
