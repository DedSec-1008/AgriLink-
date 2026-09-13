package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.LogisticsBookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogisticsBookingDao {
    @Query("SELECT * FROM logistics_bookings WHERE transactionId = :transactionId LIMIT 1")
    suspend fun getBookingForTransaction(transactionId: String): LogisticsBookingEntity?

    @Query("SELECT * FROM logistics_bookings WHERE transactionId = :transactionId LIMIT 1")
    fun getBookingForTransactionFlow(transactionId: String): Flow<LogisticsBookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: LogisticsBookingEntity)

    @Update
    suspend fun updateBooking(booking: LogisticsBookingEntity)

    @Query("SELECT COUNT(*) FROM logistics_bookings")
    suspend fun countBookings(): Int
}
