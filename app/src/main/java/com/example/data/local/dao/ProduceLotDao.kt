package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ProduceLotEntity
import com.example.model.LotStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduceLotDao {
    @Query("SELECT * FROM produce_lots ORDER BY createdAt DESC")
    fun getAllLotsFlow(): Flow<List<ProduceLotEntity>>

    @Query("SELECT * FROM produce_lots ORDER BY createdAt DESC")
    suspend fun getAllLots(): List<ProduceLotEntity>

    @Query("SELECT * FROM produce_lots WHERE lotId = :lotId LIMIT 1")
    suspend fun getLotById(lotId: String): ProduceLotEntity?

    @Query("SELECT * FROM produce_lots WHERE lotId = :lotId LIMIT 1")
    fun getLotByIdFlow(lotId: String): Flow<ProduceLotEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLot(lot: ProduceLotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLots(lots: List<ProduceLotEntity>)

    @Update
    suspend fun updateLot(lot: ProduceLotEntity)

    @Query("UPDATE produce_lots SET statusRes = :statusRes, lotStatus = :lotStatus, buyerNameRes = :buyerNameRes, updatedAt = :updatedAt WHERE lotId = :lotId")
    suspend fun updateLotStatus(
        lotId: String,
        statusRes: Int,
        lotStatus: LotStatus,
        buyerNameRes: Int?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM produce_lots WHERE lotId = :lotId")
    suspend fun deleteLotById(lotId: String)

    @Query("SELECT COUNT(*) FROM produce_lots")
    suspend fun countLots(): Int
}
