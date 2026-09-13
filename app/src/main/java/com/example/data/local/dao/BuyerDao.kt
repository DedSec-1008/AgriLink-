package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BuyerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuyerDao {
    @Query("SELECT * FROM buyers")
    fun getAllBuyersFlow(): Flow<List<BuyerEntity>>

    @Query("SELECT * FROM buyers")
    suspend fun getAllBuyers(): List<BuyerEntity>

    @Query("SELECT * FROM buyers WHERE id = :id LIMIT 1")
    suspend fun getBuyerById(id: String): BuyerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuyers(buyers: List<BuyerEntity>)

    @Query("SELECT COUNT(*) FROM buyers")
    suspend fun countBuyers(): Int
}
