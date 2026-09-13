package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.MarketPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketPriceDao {
    @Query("SELECT * FROM market_prices ORDER BY pricePerQuintal DESC")
    fun getAllPricesFlow(): Flow<List<MarketPriceEntity>>

    @Query("SELECT * FROM market_prices ORDER BY pricePerQuintal DESC")
    suspend fun getAllPrices(): List<MarketPriceEntity>

    @Query("SELECT * FROM market_prices WHERE id = :id LIMIT 1")
    suspend fun getPriceById(id: String): MarketPriceEntity?

    @Query("SELECT * FROM market_prices WHERE cropNameRes = :cropRes ORDER BY pricePerQuintal DESC")
    suspend fun getPricesForCrop(cropRes: Int): List<MarketPriceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<MarketPriceEntity>)

    @Query("SELECT COUNT(*) FROM market_prices")
    suspend fun countPrices(): Int
}
