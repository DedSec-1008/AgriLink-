package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PaymentRecordEntity
import com.example.model.PaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentRecordDao {
    @Query("SELECT * FROM payment_records WHERE transactionId = :transactionId LIMIT 1")
    suspend fun getPaymentForTransaction(transactionId: String): PaymentRecordEntity?

    @Query("SELECT * FROM payment_records WHERE transactionId = :transactionId LIMIT 1")
    fun getPaymentForTransactionFlow(transactionId: String): Flow<PaymentRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecordEntity)

    @Update
    suspend fun updatePayment(payment: PaymentRecordEntity)

    @Query("UPDATE payment_records SET status = :newStatus, updatedAt = :updatedAt WHERE transactionId = :transactionId")
    suspend fun updatePaymentStatus(
        transactionId: String,
        newStatus: PaymentStatus,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM payment_records")
    suspend fun countPayments(): Int
}
