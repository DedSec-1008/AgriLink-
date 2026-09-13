package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.LotStatus
import com.example.model.ProduceLot

/**
 * Local Room entity representing a published or drafted crop lot.
 * Indexed for fast query by farmer and status.
 */
@Entity(
    tableName = "produce_lots",
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["statusRes"]),
        Index(value = ["lotStatus"])
    ]
)
data class ProduceLotEntity(
    @PrimaryKey val lotId: String,
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
    val lotStatus: LotStatus = LotStatus.PUBLISHED,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): ProduceLot = ProduceLot(
        lotId = lotId,
        cropNameRes = cropNameRes,
        iconEmoji = iconEmoji,
        quantityQuintals = quantityQuintals,
        qualityRes = qualityRes,
        statusRes = statusRes,
        dateCreated = dateCreated,
        buyerNameRes = buyerNameRes,
        location = location,
        readyTiming = readyTiming,
        expectedPricePerQ = expectedPricePerQ,
        estimatedNetPerQ = estimatedNetPerQ,
        photos = photos,
        farmerId = farmerId,
        lotStatus = lotStatus
    )

    companion object {
        fun fromDomain(lot: ProduceLot, syncStatus: SyncStatus = SyncStatus.SYNCED): ProduceLotEntity =
            ProduceLotEntity(
                lotId = lot.lotId,
                cropNameRes = lot.cropNameRes,
                iconEmoji = lot.iconEmoji,
                quantityQuintals = lot.quantityQuintals,
                qualityRes = lot.qualityRes,
                statusRes = lot.statusRes,
                dateCreated = lot.dateCreated,
                buyerNameRes = lot.buyerNameRes,
                location = lot.location,
                readyTiming = lot.readyTiming,
                expectedPricePerQ = lot.expectedPricePerQ,
                estimatedNetPerQ = lot.estimatedNetPerQ,
                photos = lot.photos,
                farmerId = lot.farmerId,
                lotStatus = lot.lotStatus,
                syncStatus = syncStatus
            )
    }
}
