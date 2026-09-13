package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.MarketPriceInfo

/**
 * Local Room entity representing cached market prices from regional mandis.
 * Explicitly distinguishes demo/local data from future real-time live feeds.
 */
@Entity(
    tableName = "market_prices",
    indices = [
        Index(value = ["cropNameRes"])
    ]
)
data class MarketPriceEntity(
    @PrimaryKey val id: String,
    val cropNameRes: Int,
    val marketNameRes: Int,
    val pricePerQuintal: Int,
    val priceChangeTextRes: Int,
    val isPositiveChange: Boolean,
    val dataFreshness: MarketDataFreshness = MarketDataFreshness.DEMO,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomain(): MarketPriceInfo = MarketPriceInfo(
        id = id,
        cropNameRes = cropNameRes,
        marketNameRes = marketNameRes,
        pricePerQuintal = pricePerQuintal,
        priceChangeTextRes = priceChangeTextRes,
        isPositiveChange = isPositiveChange
    )

    companion object {
        fun fromDomain(
            info: MarketPriceInfo,
            freshness: MarketDataFreshness = MarketDataFreshness.DEMO
        ): MarketPriceEntity = MarketPriceEntity(
            id = info.id,
            cropNameRes = info.cropNameRes,
            marketNameRes = info.marketNameRes,
            pricePerQuintal = info.pricePerQuintal,
            priceChangeTextRes = info.priceChangeTextRes,
            isPositiveChange = info.isPositiveChange,
            dataFreshness = freshness
        )
    }
}
