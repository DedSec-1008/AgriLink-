package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.PaymentDetails
import com.example.model.PaymentStatus

/**
 * Local Room entity representing payment tracking for a completed or ongoing transaction.
 * Indexed by transactionId.
 */
@Entity(
    tableName = "payment_records",
    indices = [
        Index(value = ["transactionId"], unique = true)
    ]
)
data class PaymentRecordEntity(
    @PrimaryKey val id: String,
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
    val paymentMethod: String = "Direct Bank Transfer (RTGS/NEFT)",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): PaymentDetails = PaymentDetails(
        transactionId = transactionId,
        buyerNameRes = buyerNameRes,
        cropNameRes = cropNameRes,
        quantityQuintals = quantityQuintals,
        agreedPricePerQ = agreedPricePerQ,
        grossProduceValue = grossProduceValue,
        transportCost = transportCost,
        otherCosts = otherCosts,
        estimatedNetAmount = estimatedNetAmount,
        status = status,
        paymentDate = paymentDate,
        paymentMethod = paymentMethod
    )

    companion object {
        fun fromDomain(p: PaymentDetails, syncStatus: SyncStatus = SyncStatus.SYNCED): PaymentRecordEntity =
            PaymentRecordEntity(
                id = "PAY-${p.transactionId}",
                transactionId = p.transactionId,
                buyerNameRes = p.buyerNameRes,
                cropNameRes = p.cropNameRes,
                quantityQuintals = p.quantityQuintals,
                agreedPricePerQ = p.agreedPricePerQ,
                grossProduceValue = p.grossProduceValue,
                transportCost = p.transportCost,
                otherCosts = p.otherCosts,
                estimatedNetAmount = p.estimatedNetAmount,
                status = p.status,
                paymentDate = p.paymentDate,
                paymentMethod = p.paymentMethod,
                syncStatus = syncStatus
            )
    }
}
