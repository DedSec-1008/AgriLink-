package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TransactionEntity
import com.example.model.PaymentStatus
import com.example.model.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdTimestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY createdTimestamp DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getTransactionByIdFlow(id: String): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE lotId = :lotId LIMIT 1")
    suspend fun getTransactionByLotId(lotId: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(txs: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(tx: TransactionEntity)

    @Query("UPDATE transactions SET status = :status, paymentStatus = :paymentStatus, updatedTimestamp = :updatedAt WHERE id = :id")
    suspend fun updateTransactionStatus(
        id: String,
        status: TransactionStatus,
        paymentStatus: PaymentStatus,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE transactions SET transportDeduction = :transportCost, estimatedNetAmount = :estimatedNet, status = :status, updatedTimestamp = :updatedAt WHERE id = :id")
    suspend fun updateTransportBookingDetails(
        id: String,
        transportCost: Int,
        estimatedNet: Int,
        status: TransactionStatus,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE transactions SET buyerRating = :rating, buyerFeedback = :feedback, updatedTimestamp = :updatedAt WHERE id = :id")
    suspend fun updateBuyerRating(
        id: String,
        rating: Int,
        feedback: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun countTransactions(): Int
}
