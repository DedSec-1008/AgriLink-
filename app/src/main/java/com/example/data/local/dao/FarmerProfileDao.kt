package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FarmerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerProfileDao {
    @Query("SELECT * FROM farmer_profile WHERE id = :id LIMIT 1")
    fun getProfileFlow(id: String = "primary_farmer"): Flow<FarmerProfileEntity?>

    @Query("SELECT * FROM farmer_profile WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String = "primary_farmer"): FarmerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: FarmerProfileEntity)

    @Update
    suspend fun updateProfile(profile: FarmerProfileEntity)

    @Query("SELECT COUNT(*) FROM farmer_profile")
    suspend fun countProfiles(): Int
}
