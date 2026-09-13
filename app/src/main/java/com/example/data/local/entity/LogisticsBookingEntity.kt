package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.R
import com.example.model.TransportBooking

/**
 * Local Room entity representing a confirmed logistics booking.
 * Indexed by transactionId for quick join.
 */
@Entity(
    tableName = "logistics_bookings",
    indices = [
        Index(value = ["transactionId"], unique = true)
    ]
)
data class LogisticsBookingEntity(
    @PrimaryKey val bookingId: String,
    val transactionId: String,
    val transporterName: String,
    val vehicleTypeRes: Int,
    val pickupLocation: String,
    val deliveryLocation: String,
    val pickupTime: String,
    val distanceKm: Int = 28,
    val totalCost: Int = 6000,
    val costPerQ: Int = 120,
    val isConfirmed: Boolean = true,
    val capacityQuintals: Int = 100,
    val deliveryTimingRes: Int = R.string.delivery_today,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): TransportBooking = TransportBooking(
        bookingId = bookingId,
        transactionId = transactionId,
        transporterName = transporterName,
        vehicleTypeRes = vehicleTypeRes,
        pickupLocation = pickupLocation,
        deliveryLocation = deliveryLocation,
        pickupTime = pickupTime,
        distanceKm = distanceKm,
        totalCost = totalCost,
        costPerQ = costPerQ,
        isConfirmed = isConfirmed,
        capacityQuintals = capacityQuintals,
        deliveryTimingRes = deliveryTimingRes
    )

    companion object {
        fun fromDomain(booking: TransportBooking, syncStatus: SyncStatus = SyncStatus.SYNCED): LogisticsBookingEntity =
            LogisticsBookingEntity(
                bookingId = booking.bookingId,
                transactionId = booking.transactionId,
                transporterName = booking.transporterName,
                vehicleTypeRes = booking.vehicleTypeRes,
                pickupLocation = booking.pickupLocation,
                deliveryLocation = booking.deliveryLocation,
                pickupTime = booking.pickupTime,
                distanceKm = booking.distanceKm,
                totalCost = booking.totalCost,
                costPerQ = booking.costPerQ,
                isConfirmed = booking.isConfirmed,
                capacityQuintals = booking.capacityQuintals,
                deliveryTimingRes = booking.deliveryTimingRes,
                syncStatus = syncStatus
            )
    }
}
