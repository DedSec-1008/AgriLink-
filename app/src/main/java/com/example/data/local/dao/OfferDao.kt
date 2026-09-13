package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.OfferEntity
import com.example.model.OfferStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferDao {
    @Query("SELECT * FROM offers ORDER BY estimatedNetAmount DESC")
    fun getAllOffersFlow(): Flow<List<OfferEntity>>

    @Query("SELECT * FROM offers WHERE lotId = :lotId ORDER BY estimatedNetAmount DESC")
    fun getOffersForLotFlow(lotId: String): Flow<List<OfferEntity>>

    @Query("SELECT * FROM offers WHERE lotId = :lotId ORDER BY estimatedNetAmount DESC")
    suspend fun getOffersForLot(lotId: String): List<OfferEntity>

    @Query("SELECT * FROM offers WHERE id = :offerId LIMIT 1")
    suspend fun getOfferById(offerId: String): OfferEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: OfferEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<OfferEntity>)

    @Update
    suspend fun updateOffer(offer: OfferEntity)

    @Query("UPDATE offers SET status = :newStatus, updatedTimestamp = :updatedAt WHERE id = :offerId")
    suspend fun updateOfferStatus(
        offerId: String,
        newStatus: OfferStatus,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE offers SET status = 'CANCELLED', updatedTimestamp = :updatedAt WHERE lotId = :lotId AND id != :acceptedOfferId")
    suspend fun cancelOtherOffersForLot(
        lotId: String,
        acceptedOfferId: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM offers")
    suspend fun countOffers(): Int
}
