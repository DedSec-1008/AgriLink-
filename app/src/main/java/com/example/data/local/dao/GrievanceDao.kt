package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.GrievanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrievanceDao {
    @Query("SELECT * FROM grievances WHERE transactionId = :transactionId ORDER BY createdTimestamp DESC")
    suspend fun getGrievancesForTransaction(transactionId: String): List<GrievanceEntity>

    @Query("SELECT * FROM grievances WHERE transactionId = :transactionId ORDER BY createdTimestamp DESC")
    fun getGrievancesForTransactionFlow(transactionId: String): Flow<List<GrievanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrievance(grievance: GrievanceEntity)

    @Query("SELECT COUNT(*) FROM grievances")
    suspend fun countGrievances(): Int
}
